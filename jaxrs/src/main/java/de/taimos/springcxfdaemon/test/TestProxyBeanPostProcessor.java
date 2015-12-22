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

package de.taimos.springcxfdaemon.test;

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
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.ext.Provider;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
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
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringValueResolver;

@SuppressWarnings("serial")
public class TestProxyBeanPostProcessor implements InstantiationAwareBeanPostProcessor, EmbeddedValueResolverAware, BeanFactoryAware, Serializable {
	
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
				if (field.isAnnotationPresent(TestProxy.class)) {
					if (Modifier.isStatic(field.getModifiers())) {
						throw new IllegalStateException("@TestProxy annotation is not supported on static fields");
					}
					currElements.add(new TestProxyElement(field, null));
				}
			}
			for (Method method : targetClass.getDeclaredMethods()) {
				Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
				if (!BridgeMethodResolver.isVisibilityBridgeMethodPair(method, bridgedMethod)) {
					continue;
				}
				if (method.equals(ClassUtils.getMostSpecificMethod(method, clazz))) {
					if (bridgedMethod.isAnnotationPresent(TestProxy.class)) {
						if (Modifier.isStatic(method.getModifiers())) {
							throw new IllegalStateException("@TestProxy annotation is not supported on static methods");
						}
						if (method.getParameterTypes().length != 1) {
							throw new IllegalStateException("@TestProxy annotation requires a single-arg method: " + method);
						}
						PropertyDescriptor pd = BeanUtils.findPropertyForMethod(bridgedMethod, clazz);
						currElements.add(new TestProxyElement(method, pd));
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
	 * or setter method, supporting the @TestProxy annotation.
	 */
	private class TestProxyElement extends InjectionMetadata.InjectedElement {
		
		public TestProxyElement(Member member, PropertyDescriptor pd) {
			super(member, pd);
		}
		
		private DependencyDescriptor getDependencyDescriptor() {
			if (this.isField) {
				return new DependencyDescriptor((Field) this.member, true);
			}
			return new DependencyDescriptor(new MethodParameter((Method) this.member, 0), true);
		}
		
		@Override
		@SuppressWarnings("unchecked")
		protected Object getResourceToInject(Object target, String requestingBeanName) {
			List<Object> providers = new ArrayList<>();
			try {
				String componentAnnotation = TestProxyBeanPostProcessor.this.resolver.resolveStringValue("${jaxrs.annotation:de.taimos.springcxfdaemon.JaxRsComponent}");
				Class<? extends Annotation> componentAnnotationClazz = (Class<? extends Annotation>) Class.forName(componentAnnotation);
				String[] beans = TestProxyBeanPostProcessor.this.beanFactory.getBeanNamesForAnnotation(componentAnnotationClazz);
				for (String bean : beans) {
					if (TestProxyBeanPostProcessor.this.beanFactory.findAnnotationOnBean(bean, Provider.class) != null) {
						providers.add(TestProxyBeanPostProcessor.this.beanFactory.getBean(bean));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			String url = TestProxyBeanPostProcessor.this.resolver.resolveStringValue("${server.url:http://localhost:${jaxrs.bindport:${svc.port:8080}}}");
			return JAXRSClientFactory.create(url, this.getDependencyDescriptor().getDependencyType(), providers);
		}
	}
	
}
