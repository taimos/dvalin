package de.taimos.dvalin.i18n;

import de.taimos.dvalin.i18n.xml.I18nXMLHandler;
import de.taimos.dvalin.test.AbstractMockitoTest;
import de.taimos.dvalin.test.inject.InjectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author psigloch
 */
public class ResourceFrameworkTest extends AbstractMockitoTest {

    private I18nLoader resourceAccess = new I18nLoader();

    private I18nXMLHandler xmlHandler = new I18nXMLHandler();

    private static Resource findResource(String path) {
        return new ClassPathResource(path);
    }

    private static void injectValue(Object bean, String field, Resource[] value) {
        try {
            Field beanField = ResourceFrameworkTest.getField(bean.getClass(), field);
            if(beanField.isAnnotationPresent(Value.class)) {
                ResourceFrameworkTest.doInjection(bean, value, beanField);
            } else {
                throw new RuntimeException("Did not find field " + field + " of type String to inject value");
            }
        } catch(NoSuchFieldException e) {
            throw new RuntimeException("Did not find field " + field + " to inject value");
        } catch(SecurityException e) {
            throw new RuntimeException("Error injecting value due to access violation", e);
        }
    }

    private static Field getField(Class beanClass, String fieldName) throws NoSuchFieldException {
        try {
            return beanClass.getDeclaredField(fieldName);
        } catch(NoSuchFieldException e) {
            if(!beanClass.getSuperclass().equals(Object.class)) {
                return ResourceFrameworkTest.getField(beanClass.getSuperclass(), fieldName);
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
        } catch(IllegalAccessException e) {
            throw new RuntimeException("Error injecting dependency due to access violation", e);
        }
    }

    @Before
    public void setUp() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        InjectionUtils.injectValue(this.resourceAccess, "DEFAULT_LOCALE_STRING", "de");

        Resource xml = ResourceFrameworkTest.findResource("i18n/test.xml");
        ResourceFrameworkTest.injectValue(this.xmlHandler, "resourceFiles", new Resource[]{xml});
        Resource schema = ResourceFrameworkTest.findResource("schema/i18nSchema_v1.xsd");
        ResourceFrameworkTest.injectValue(this.xmlHandler, "resourceSchema", new Resource[]{schema});
        List<II18nResourceHandler> resourceHandlers = new ArrayList<>();
        resourceHandlers.add(this.xmlHandler);
        InjectionUtils.inject(this.resourceAccess, resourceHandlers );

        Method m = this.resourceAccess.getClass().getDeclaredMethod("initializeResources");
        m.setAccessible(true);
        m.invoke(this.resourceAccess);
    }

    @Test
    public void testNonExisting() {
        String notExisting = this.resourceAccess.getString("notExisting");
        Assert.assertEquals(notExisting, "!notExisting!");
    }

    @Test
    public void testBasicLanguageGet() {
        {
            //default locale
            String text = this.resourceAccess.getString("textA");
            Assert.assertEquals(text, "TextAGerman");
        }

        {
            //fixed locale
            String text = this.resourceAccess.getString(Locale.ENGLISH, "textA");
            Assert.assertEquals(text, "TextAEnglish");
        }

        {
            //nonexistant locale -> default to default language
            String text = this.resourceAccess.getString(Locale.ITALIAN, "textA");
            Assert.assertEquals(text, "TextAGerman");
        }
    }

    @Test
    public void testGetWithReplaces() {
        {
            //default locale
            String text = this.resourceAccess.getString("textB", "aTest1", "aTest2", "aTest3");
            Assert.assertEquals(text, "TextBGerman aTest1 aTest3");
        }

        {
            //fixed locale
            String text = this.resourceAccess.getString(Locale.ENGLISH, "textB", "aTest1", "aTest2", "aTest3");
            Assert.assertEquals(text, "TextBEnglish aTest1 aTest3");
        }

        {
            //nonexistant locale -> default to default language
            String text = this.resourceAccess.getString(Locale.ITALIAN, "textB", "aTest1", "aTest2", "aTest3");
            Assert.assertEquals(text, "TextBGerman aTest1 aTest3");
        }
    }

    @Test
    public void testGetEnums() {
        {
            //default locale
            String text = this.resourceAccess.getString(TestEnum.FIELD_A);
            Assert.assertEquals(text, "EnumFieldAGerman");
        }

        {
            //fixed locale
            String text = this.resourceAccess.getString(Locale.ENGLISH, TestEnum.FIELD_A);
            Assert.assertEquals(text, "EnumFieldAEnglish");
        }

        {
            //nonexistant locale -> default to default language
            String text = this.resourceAccess.getString(Locale.ITALIAN, TestEnum.FIELD_B);
            Assert.assertEquals(text, "EnumFieldBGerman");
        }
    }
}
