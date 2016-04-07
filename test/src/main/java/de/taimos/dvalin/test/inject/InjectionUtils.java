/*
 * Copyright (c) 2016. Taimos GmbH http://www.taimos.de
 */

package de.taimos.dvalin.test.inject;

import java.lang.reflect.Field;

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
        Class<?> beanClass = bean.getClass();
        Field[] declaredFields = beanClass.getDeclaredFields();
        for (Field field : declaredFields) {
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
            Class<?> beanClass = bean.getClass();
            Field beanField = beanClass.getDeclaredField(field);
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
        Class<?> beanClass = bean.getClass();
        Field[] declaredFields = beanClass.getDeclaredFields();
        boolean found = false;
        for (Field field : declaredFields) {
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
}
