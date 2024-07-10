package de.taimos.dvalin.i18n;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import de.taimos.dvalin.test.AbstractMockitoTest;
import de.taimos.dvalin.test.inject.InjectionUtils;

/**
 * Copyright 2018 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public abstract class AbstractResourceFrameworkTest extends AbstractMockitoTest {

    protected I18nLoader resourceAccess = new I18nLoader();

    protected static Resource findResource(String path) {
        return new ClassPathResource(path);
    }

    protected static void injectValue(Object bean, String field, Resource[] value) {
        try {
            Field beanField = AbstractResourceFrameworkTest.getField(bean.getClass(), field);
            if (beanField.isAnnotationPresent(Value.class)) {
                AbstractResourceFrameworkTest.doInjection(bean, value, beanField);
            } else {
                throw new RuntimeException("Did not find field " + field + " of type String to inject value");
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Did not find field " + field + " to inject value");
        } catch (SecurityException e) {
            throw new RuntimeException("Error injecting value due to access violation", e);
        }
    }

    private static Field getField(Class beanClass, String fieldName) throws NoSuchFieldException {
        try {
            return beanClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            if (!beanClass.getSuperclass().equals(Object.class)) {
                return AbstractResourceFrameworkTest.getField(beanClass.getSuperclass(), fieldName);
            }
            throw e;
        }
    }

    private static void doInjection(Object bean, Object dependency, Field field) {
        try {
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            field.set(bean, dependency);
            field.setAccessible(accessible);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error injecting dependency due to access violation", e);
        }
    }

    protected abstract List<II18nResourceHandler> getResourceHandler();

    @BeforeEach
    public void setUp() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        InjectionUtils.injectValue(this.resourceAccess, "DEFAULT_LOCALE_STRING", "de");
        InjectionUtils.inject(this.resourceAccess, this.getResourceHandler());

        Method m = this.resourceAccess.getClass().getDeclaredMethod("initializeResources");
        m.setAccessible(true);
        m.invoke(this.resourceAccess);
    }

    @Test
    public void testNonExisting() {
        String notExisting = this.resourceAccess.getString("notExisting");
        Assertions.assertEquals("!notExisting!", notExisting);
    }

    @Test
    public void testBasicLanguageGet() {
        {
            //default locale
            String text = this.resourceAccess.getString("textA");
            Assertions.assertEquals("TextAGerman", text);
        }

        {
            //fixed locale
            String text = this.resourceAccess.getString(Locale.ENGLISH, "textA");
            Assertions.assertEquals("TextAEnglish", text);
        }

        {
            //nonexistant locale -> default to default language
            String text = this.resourceAccess.getString(Locale.ITALIAN, "textA");
            Assertions.assertEquals("TextAGerman", text);
        }
    }

    @Test
    public void testGetWithReplaces() {
        {
            //default locale
            String text = this.resourceAccess.getString("textB", "aTest1", "aTest2", "aTest3");
            Assertions.assertEquals("TextBGerman aTest1 aTest3", text);
        }

        {
            //fixed locale
            String text = this.resourceAccess.getString(Locale.ENGLISH, "textB", "aTest1", "aTest2", "aTest3");
            Assertions.assertEquals("TextBEnglish aTest1 aTest3", text);
        }

        {
            //nonexistant locale -> default to default language
            String text = this.resourceAccess.getString(Locale.ITALIAN, "textB", "aTest1", "aTest2", "aTest3");
            Assertions.assertEquals("TextBGerman aTest1 aTest3", text);
        }
    }

    @Test
    public void testGetEnums() {
        {
            //default locale
            String text = this.resourceAccess.getString(TestEnum.FIELD_A);
            Assertions.assertEquals("EnumFieldAGerman", text);
        }

        {
            //fixed locale
            String text = this.resourceAccess.getString(Locale.ENGLISH, TestEnum.FIELD_A);
            Assertions.assertEquals("EnumFieldAEnglish", text);
        }

        {
            //nonexistant locale -> default to default language
            String text = this.resourceAccess.getString(Locale.ITALIAN, TestEnum.FIELD_B);
            Assertions.assertEquals("EnumFieldBGerman", text);
        }
    }
}
