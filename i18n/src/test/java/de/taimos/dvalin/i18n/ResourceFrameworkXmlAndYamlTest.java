package de.taimos.dvalin.i18n;

import de.taimos.dvalin.i18n.xml.I18nXMLHandler;
import de.taimos.dvalin.i18n.yaml.I18nYAMLHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author mweise
 */
public class ResourceFrameworkXmlAndYamlTest extends AbstractResourceFrameworkTest {

    @Override
    protected List<II18nResourceHandler> getResourceHandler() {
        List<II18nResourceHandler> resourceHandlers = new ArrayList<>();

        I18nXMLHandler xmlHandler = new I18nXMLHandler();
        Resource xml = AbstractResourceFrameworkTest.findResource("i18n/test.xml");
        AbstractResourceFrameworkTest.injectValue(xmlHandler, "resourceFiles", new Resource[]{xml});
        Resource schema = AbstractResourceFrameworkTest.findResource("schema/i18nSchema_v1.xsd");
        AbstractResourceFrameworkTest.injectValue(xmlHandler, "resourceSchema", new Resource[]{schema});
        resourceHandlers.add(xmlHandler);

        I18nYAMLHandler yamlHandler = new I18nYAMLHandler();
        Resource yaml = AbstractResourceFrameworkTest.findResource("i18n/test.yaml");
        AbstractResourceFrameworkTest.injectValue(yamlHandler, "resourceFiles", new Resource[]{yaml});
        resourceHandlers.add(yamlHandler);
        return resourceHandlers;
    }

    @Test
    public void testXMLandYAMLFiles() {
        {
            //default locale
            String text = this.resourceAccess.getString("xmlOnly");
            Assertions.assertEquals("nur in XML", text);
        }

        {
            //fixed locale
            String text = this.resourceAccess.getString(Locale.ENGLISH, "yamlOnly");
            Assertions.assertEquals("only in YAML", text);
        }
    }
}
