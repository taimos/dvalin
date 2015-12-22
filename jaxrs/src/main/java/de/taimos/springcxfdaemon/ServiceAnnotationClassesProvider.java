package de.taimos.springcxfdaemon;

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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ServiceAnnotationClassesProvider {
	
	private Class<? extends Annotation> serviceAnnotation;
	
	@Autowired
	private ListableBeanFactory beanFactory;
	
	
	public Class<? extends Annotation> getServiceAnnotation() {
		return this.serviceAnnotation;
	}
	
	public void setServiceAnnotation(Class<? extends Annotation> serviceAnnotation) {
		this.serviceAnnotation = serviceAnnotation;
	}
	
	public Class<?>[] getClasses() {
		Map<String, Object> beansWithAnnotation = this.beanFactory.getBeansWithAnnotation(this.serviceAnnotation);
		List<Class<?>> classes = new ArrayList<>();
		for (Object bean : beansWithAnnotation.values()) {
			classes.add(bean.getClass());
			classes.addAll(Arrays.asList(bean.getClass().getInterfaces()));
		}
		return classes.toArray(new Class[0]);
	}
}
