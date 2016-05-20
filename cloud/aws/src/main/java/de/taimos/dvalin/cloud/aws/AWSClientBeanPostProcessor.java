/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.taimos.dvalin.cloud.aws;

/*
 * #%L
 * Dvalin cloud aws library
 * %%
 * Copyright (C) 2016 Taimos GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.amazonaws.AmazonWebServiceClient;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringValueResolver;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;

@Component
@SuppressWarnings("serial")
public class AWSClientBeanPostProcessor implements InstantiationAwareBeanPostProcessor, EmbeddedValueResolverAware, BeanFactoryAware, Serializable {

    private transient ConfigurableListableBeanFactory beanFactory;
    private StringValueResolver resolver;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        Assert.notNull(beanFactory, "BeanFactory must not be null");
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        return null;
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        return true;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {
        InjectionMetadata metadata = this.buildResourceMetadata(bean.getClass());
        try {
            metadata.inject(bean, beanName, pvs);
        } catch (Throwable ex) {
            throw new BeanCreationException(beanName, "Injection of resource dependencies failed", ex);
        }
        return pvs;
    }

    private InjectionMetadata buildResourceMetadata(Class<?> clazz) {
        LinkedList<InjectionMetadata.InjectedElement> elements = new LinkedList<>();
        Class<?> targetClass = clazz;

        do {
            LinkedList<InjectionMetadata.InjectedElement> currElements = new LinkedList<>();
            for (Field field : targetClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(AWSClient.class)) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        throw new IllegalStateException("@AWSClient annotation is not supported on static fields");
                    }
                    currElements.add(new AWSClientElement(field, null, field.getAnnotation(AWSClient.class)));
                }
            }
            for (Method method : targetClass.getDeclaredMethods()) {
                Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
                if (!BridgeMethodResolver.isVisibilityBridgeMethodPair(method, bridgedMethod)) {
                    continue;
                }
                if (method.equals(ClassUtils.getMostSpecificMethod(method, clazz))) {
                    if (bridgedMethod.isAnnotationPresent(AWSClient.class)) {
                        if (Modifier.isStatic(method.getModifiers())) {
                            throw new IllegalStateException("@AWSClient annotation is not supported on static methods");
                        }
                        if (method.getParameterTypes().length != 1) {
                            throw new IllegalStateException("@AWSClient annotation requires a single-arg method: " + method);
                        }
                        PropertyDescriptor pd = BeanUtils.findPropertyForMethod(bridgedMethod, clazz);
                        currElements.add(new AWSClientElement(method, pd, method.getAnnotation(AWSClient.class)));
                    }
                }
            }
            elements.addAll(0, currElements);
            targetClass = targetClass.getSuperclass();
        } while ((targetClass != null) && (targetClass != Object.class));

        return new InjectionMetadata(clazz, elements);
    }

    /**
     * Class representing injection information about an annotated field
     * or setter method, supporting the @Interconnect annotation.
     */
    private class AWSClientElement extends InjectionMetadata.InjectedElement {

        private final Logger LOGGER = LoggerFactory.getLogger(AWSClientElement.class);

        private final AWSClient client;

        public AWSClientElement(Member member, PropertyDescriptor pd, AWSClient client) {
            super(member, pd);
            this.client = client;
        }

        private DependencyDescriptor getDependencyDescriptor() {
            if (this.isField) {
                return new DependencyDescriptor((Field) this.member, true);
            }
            return new DependencyDescriptor(new MethodParameter((Method) this.member, 0), true);
        }

        @Override
        protected Object getResourceToInject(Object target, String requestingBeanName) {
            Class<?> dependencyType = this.getDependencyDescriptor().getDependencyType();
            if (!AmazonWebServiceClient.class.isAssignableFrom(dependencyType)) {
                throw new RuntimeException("Field has to be of type AmazonWebServiceClient but was of type " + dependencyType.getCanonicalName());
            }
            Region region = this.getRegion(this.client);
            this.LOGGER.info("Using AWS region {}", region.toString());

            AWSCredentials cred = this.getStaticCredentials();

            final AWSCredentialsProvider provider;
            if (cred != null) {
                provider = new AWSCredentialsProviderChain(new StaticCredentialsProvider(cred));
            } else {
                provider = new DefaultAWSCredentialsProviderChain();
            }
            AmazonWebServiceClient client = region.createClient((Class<? extends AmazonWebServiceClient>) dependencyType, provider, null);
            String endpoint = this.getCustomEndpoint(this.client);
            if (endpoint != null) {
                client.setEndpoint(endpoint);
            }
            return client;
        }

        private String getCustomEndpoint(AWSClient client) {
            if (!client.endpoint().isEmpty()) {
                try {
                    String endpointString = AWSClientBeanPostProcessor.this.resolver.resolveStringValue(this.client.endpoint());
                    if (!endpointString.isEmpty()) {
                        return endpointString;
                    }
                } catch (IllegalArgumentException e) {
                    this.LOGGER.warn("Failed to read endpoint property", e);
                }
            }
            return null;
        }

        private AWSCredentials getStaticCredentials() {
            try {
                String accessKey = AWSClientBeanPostProcessor.this.resolver.resolveStringValue("${aws.accessKeyId}");
                String secretKey = AWSClientBeanPostProcessor.this.resolver.resolveStringValue("${aws.secretKey}");
                if (!accessKey.isEmpty() && !secretKey.isEmpty()) {
					return new BasicAWSCredentials(accessKey, secretKey);
				}
            } catch (IllegalArgumentException e) {
                //
            }
            return null;
        }

        private Region getRegion(AWSClient client) {
            if (!client.region().isEmpty()) {
                try {
                    String regionString = AWSClientBeanPostProcessor.this.resolver.resolveStringValue(this.client.region());
                    if (!regionString.isEmpty()) {
                        return Region.getRegion(Regions.fromName(regionString));
                    }
                } catch (IllegalArgumentException e) {
                    this.LOGGER.warn("Failed to read regionProperty", e);
                }
            }
            try {
                String regionString = AWSClientBeanPostProcessor.this.resolver.resolveStringValue("${aws.region}");
                if (!regionString.isEmpty()) {
                    return Region.getRegion(Regions.fromName(regionString));
                }
            } catch (IllegalArgumentException e) {
                this.LOGGER.info("Did not find aws.region system property");
            }
            if (System.getenv("AWS_DEFAULT_REGION") != null) {
                return Region.getRegion(Regions.fromName(System.getenv("AWS_DEFAULT_REGION")));
            }
            if (System.getenv("AWS_REGION") != null) {
                return Region.getRegion(Regions.fromName(System.getenv("AWS_REGION")));
            }
            Region currentRegion = Regions.getCurrentRegion();
            if (currentRegion != null) {
                return currentRegion;
            }
            return Region.getRegion(Regions.DEFAULT_REGION);
        }
    }

}
