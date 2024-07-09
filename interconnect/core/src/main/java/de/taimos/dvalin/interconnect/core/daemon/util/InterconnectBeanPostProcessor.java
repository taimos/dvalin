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

package de.taimos.dvalin.interconnect.core.daemon.util;

/*
 * #%L
 * Dvalin interconnect core library
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


import de.taimos.dvalin.interconnect.core.daemon.Interconnect;
import de.taimos.dvalin.interconnect.core.daemon.proxy.DefaultDaemonProxyFactory;
import de.taimos.dvalin.interconnect.model.service.IDaemon;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.annotation.InjectionMetadata.InjectedElement;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;

/**
 * @author thoeger
 */
@Component
@SuppressWarnings("serial")
public class InterconnectBeanPostProcessor
    implements InstantiationAwareBeanPostProcessor, BeanFactoryAware, Serializable {

    private transient DefaultDaemonProxyFactory proxyFactory;

    @Override
    public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
        Assert.notNull(beanFactory, "BeanFactory must not be null");
        this.proxyFactory = beanFactory.getBean(DefaultDaemonProxyFactory.class);
    }

    @Override
    public PropertyValues postProcessProperties(@NonNull PropertyValues pvs, Object bean, @NonNull String beanName) throws BeansException {
        InjectionMetadata metadata = this.buildResourceMetadata(bean.getClass());
        try {
            metadata.inject(bean, beanName, pvs);
        } catch (Throwable ex) {
            throw new BeanCreationException(beanName, "Injection of resource dependencies failed", ex);
        }
        return pvs;
    }

    @Override
    @Deprecated
    public PropertyValues postProcessPropertyValues(@NonNull PropertyValues pvs, @NonNull PropertyDescriptor[] pds, @NonNull Object bean, @NonNull String beanName) throws BeansException {
        return this.postProcessProperties(pvs, bean, beanName);
    }

    private InjectionMetadata buildResourceMetadata(Class<?> clazz) {
        LinkedList<InjectionMetadata.InjectedElement> elements = new LinkedList<>();
        Class<?> targetClass = clazz;

        do {
            LinkedList<InjectionMetadata.InjectedElement> currElements = new LinkedList<>();
            currElements.addAll(this.getFieldInjections(targetClass));
            currElements.addAll(this.getMethodInjections(clazz, targetClass));
            elements.addAll(0, currElements);
            targetClass = targetClass.getSuperclass();
        } while ((targetClass != null) && (targetClass != Object.class));

        return new InjectionMetadata(clazz, elements);
    }

    private LinkedList<InjectedElement> getFieldInjections(Class<?> targetClass) {
        LinkedList<InjectedElement> currElements = new LinkedList<>();
        for (Field field : targetClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Interconnect.class)) {
                if (Modifier.isStatic(field.getModifiers())) {
                    throw new IllegalStateException("@Interconnect annotation is not supported on static fields");
                }
                currElements.add(new InterconnectElement(field, null));
            }
        }
        return currElements;
    }

    private LinkedList<InjectedElement> getMethodInjections(Class<?> clazz, Class<?> targetClass) {
        LinkedList<InjectedElement> currElements = new LinkedList<>();
        for (Method method : targetClass.getDeclaredMethods()) {
            Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
            if (!BridgeMethodResolver.isVisibilityBridgeMethodPair(method, bridgedMethod)) {
                continue;
            }
            if (method.equals(ClassUtils.getMostSpecificMethod(method, clazz))) {
                if (bridgedMethod.isAnnotationPresent(Interconnect.class)) {
                    if (Modifier.isStatic(method.getModifiers())) {
                        throw new IllegalStateException("@Interconnect annotation is not supported on static methods");
                    }
                    if (method.getParameterTypes().length != 1) {
                        throw new IllegalStateException(
                            "@Interconnect annotation requires a single-arg method: " + method);
                    }
                    PropertyDescriptor pd = BeanUtils.findPropertyForMethod(bridgedMethod, clazz);
                    currElements.add(new InterconnectElement(method, pd));
                }
            }
        }
        return currElements;
    }


    /**
     * Class representing injection information about an annotated field
     * or setter method, supporting the @Interconnect annotation.
     */
    private class InterconnectElement extends InjectionMetadata.InjectedElement {

        public InterconnectElement(Member member, PropertyDescriptor pd) {
            super(member, pd);
        }

        private DependencyDescriptor getDependencyDescriptor() {
            if (this.isField) {
                return new DependencyDescriptor((Field) this.member, true);
            }
            return new DependencyDescriptor(new MethodParameter((Method) this.member, 0), true);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Object getResourceToInject(@NonNull Object target, String requestingBeanName) {
            Class<?> dependencyType = this.getDependencyDescriptor().getDependencyType();
            if (!IDaemon.class.isAssignableFrom(dependencyType)) {
                throw new RuntimeException(
                    "Field has to be of type IDaemon but was of type " + dependencyType.getCanonicalName());
            }
            return InterconnectBeanPostProcessor.this.proxyFactory.create((Class<? extends IDaemon>) dependencyType);
        }
    }

}
