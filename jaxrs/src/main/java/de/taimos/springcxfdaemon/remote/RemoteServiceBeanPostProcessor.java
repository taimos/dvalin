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

package de.taimos.springcxfdaemon.remote;

/*
 * #%L
 * Daemon with Spring and CXF
 * %%
 * Copyright (C) 2013 - 2015 Taimos GmbH
 * %%
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
 * #L%
 */

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringValueResolver;

@SuppressWarnings("serial")
public class RemoteServiceBeanPostProcessor implements InstantiationAwareBeanPostProcessor, EmbeddedValueResolverAware, BeanFactoryAware, Serializable {
	
	private transient ConfigurableListableBeanFactory beanFactory;
	private transient StringValueResolver resolver;
	
	
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
		LinkedList<InjectionMetadata.InjectedElement> elements = new LinkedList<InjectionMetadata.InjectedElement>();
		Class<?> targetClass = clazz;
		
		do {
			LinkedList<InjectionMetadata.InjectedElement> currElements = new LinkedList<InjectionMetadata.InjectedElement>();
			for (Field field : targetClass.getDeclaredFields()) {
				if (field.isAnnotationPresent(RemoteService.class)) {
					if (Modifier.isStatic(field.getModifiers())) {
						throw new IllegalStateException("@RemoteService annotation is not supported on static fields");
					}
					currElements.add(new RemoteServiceElement(field, field, null));
				}
			}
			for (Method method : targetClass.getDeclaredMethods()) {
				Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
				if (!BridgeMethodResolver.isVisibilityBridgeMethodPair(method, bridgedMethod)) {
					continue;
				}
				if (method.equals(ClassUtils.getMostSpecificMethod(method, clazz))) {
					if (bridgedMethod.isAnnotationPresent(RemoteService.class)) {
						if (Modifier.isStatic(method.getModifiers())) {
							throw new IllegalStateException("@RemoteService annotation is not supported on static methods");
						}
						if (method.getParameterTypes().length != 1) {
							throw new IllegalStateException("@RemoteService annotation requires a single-arg method: " + method);
						}
						PropertyDescriptor pd = BeanUtils.findPropertyForMethod(bridgedMethod, clazz);
						currElements.add(new RemoteServiceElement(method, bridgedMethod, pd));
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
	 * or setter method, supporting the @RemoteService annotation.
	 */
	private class RemoteServiceElement extends InjectionMetadata.InjectedElement {
		
		private String serviceName;
		private String baseURL;
		
		
		public RemoteServiceElement(Member member, AnnotatedElement ae, PropertyDescriptor pd) {
			super(member, pd);
			RemoteService resource = ae.getAnnotation(RemoteService.class);
			this.serviceName = resource.name();
			this.baseURL = resource.baseURL();
		}
		
		private DependencyDescriptor getDependencyDescriptor() {
			if (this.isField) {
				return new DependencyDescriptor((Field) this.member, true);
			}
			return new DependencyDescriptor(new MethodParameter((Method) this.member, 0), true);
		}
		
		@Override
		protected Object getResourceToInject(Object target, String requestingBeanName) {
			Object value;
			try {
				value = RemoteServiceBeanPostProcessor.this.beanFactory.resolveDependency(this.getDependencyDescriptor(), requestingBeanName);
			} catch (NoSuchBeanDefinitionException notFound) {
				final String url;
				if ((this.serviceName != null) && !this.serviceName.isEmpty()) {
					String proto = RemoteServiceBeanPostProcessor.this.resolver.resolveStringValue(String.format("${%s.protocol:http}", this.serviceName));
					String host = RemoteServiceBeanPostProcessor.this.resolver.resolveStringValue(String.format("${%s.host:localhost}", this.serviceName));
					String port = RemoteServiceBeanPostProcessor.this.resolver.resolveStringValue(String.format("${%s.port}", this.serviceName));
					url = String.format("%s://%s:%s", proto, host, port);
				} else if ((this.baseURL != null) && !this.baseURL.isEmpty()) {
					url = this.baseURL;
				} else {
					throw new RuntimeException("Either service name or base URL must not be empty");
				}
				value = JAXRSClientFactory.create(url, this.getDependencyDescriptor().getDependencyType()); // TODO add providers
			}
			return value;
		}
	}
	
}
