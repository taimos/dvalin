/*
 * Copyright (c) 2016. Taimos GmbH http://www.taimos.de
 */

package de.taimos.dvalin.test.inject;

/*-
 * #%L
 * Test support for dvalin
 * %%
 * Copyright (C) 2016 - 2017 Taimos GmbH
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

public class InjectionUtils {

    public static final Logger LOGGER = LoggerFactory.getLogger(InjectionUtils.class);

    /**
     * inject mocks into all autowired fields and return them to the caller
     *
     * @param bean the bean to autowire
     * @return object containing all the created and injected mocks
     */
    public static InjectionMock injectMocks(Object bean) {
        InjectionMock mock = new InjectionMock();
        List<Field> fields = getFields(bean.getClass());
        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class)) {
                Qualifier qualifierAnnotation = field.getAnnotation(Qualifier.class);
                String qualifier = qualifierAnnotation == null ? null : qualifierAnnotation.value();

                Object mockObject = Mockito.mock(field.getType());
                doInjection(bean, mockObject, field);
                mock.addMock(field.getName(), mockObject, field.getType(), qualifier);
            }
        }
        return mock;
    }

    /**
     * inject String in field that is annotated with <em>@Value</em>
     *
     * @param bean  the bean to inject the value into
     * @param field the name of the target field
     * @param value the value to inject
     */
    public static void injectValue(Object bean, String field, String value) {
        try {
            Field beanField = getField(bean.getClass(), field);
            if (beanField.isAnnotationPresent(Value.class) && beanField.getType().equals(String.class)) {
                doInjection(bean, value, beanField);
            } else {
                throw new RuntimeException("Did not find field " + field + " of type String to inject value");
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Did not find field " + field + " to inject value");
        } catch (SecurityException e) {
            throw new RuntimeException("Error injecting value due to access violation", e);
        }
    }

    /**
     * inject objects into the bean
     *
     * @param bean         the bean to inject the dependencies into
     * @param dependencies the objects to inject
     */
    public static void inject(Object bean, Object... dependencies) {
        for (Object dependency : dependencies) {
            InjectionUtils.inject(bean, dependency);
        }
    }

    /**
     * inject the given object into the according field annotated with <em>@Autowired</em>
     *
     * @param bean       the bean to inject the dependency into
     * @param dependency the object to inject
     */
    public static void inject(Object bean, Object dependency) {
        InjectionUtils.inject(bean, null, dependency);
    }

    /**
     * inject the given object into the according field annotated with <em>@Autowired</em><br>
     * additionally the <em>@Qualifier</em> annotation is evaluated to find the correct field
     *
     * @param bean       the bean to inject the dependency into
     * @param qualifier  the qualifier to match if <em>@Qualifier</em> is present on the field
     * @param dependency the object to inject
     */
    public static void inject(Object bean, String qualifier, Object dependency) {
        List<Field> fields = getFields(bean.getClass());
        boolean found = false;
        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class) && field.getType().isAssignableFrom(dependency.getClass())) {
                Qualifier qualifierAnnotation = field.getAnnotation(Qualifier.class);
                if ((qualifierAnnotation == null) || qualifierAnnotation.value().equals(qualifier)) {
                    doInjection(bean, dependency, field);
                    found = true;
                }
            }
        }
        if (!found) {
            throw new RuntimeException("Did not find field to inject object of type " + dependency.getClass());
        }
    }

    private static void doInjection(Object bean, Object dependency, Field field) {
        try {
            LOGGER.info("Injecting object of type {} into bean of type {} in field {}", dependency.getClass(), bean.getClass(), field.getName());
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            field.set(bean, dependency);
            field.setAccessible(accessible);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error injecting dependency due to access violation", e);
        }
    }

    private static List<Field> getFields(Class beanClass) {
        List<Field> fields = new ArrayList<>();
        Collections.addAll(fields, beanClass.getDeclaredFields());
        if (!beanClass.getSuperclass().equals(Object.class)) {
            fields.addAll(getFields(beanClass.getSuperclass()));
        }
        return fields;
    }

    private static Field getField(Class beanClass, String fieldName) throws NoSuchFieldException {
        try {
            return beanClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            if (!beanClass.getSuperclass().equals(Object.class)) {
                return getField(beanClass.getSuperclass(), fieldName);
            }
            throw e;
        }
    }
}
