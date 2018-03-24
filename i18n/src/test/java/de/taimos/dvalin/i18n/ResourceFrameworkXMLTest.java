package de.taimos.dvalin.i18n;

import de.taimos.dvalin.i18n.xml.I18nXMLHandler;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * @author psigloch
 */
public class ResourceFrameworkXMLTest extends AbstractResourceFrameworkTest {

    @Override
    protected List<II18nResourceHandler> getResourceHandler() {
        I18nXMLHandler xmlHandler = new I18nXMLHandler();
        Resource xml = AbstractResourceFrameworkTest.findResource("i18n/test.xml");
        AbstractResourceFrameworkTest.injectValue(xmlHandler, "resourceFiles", new Resource[]{xml});
        Resource schema = AbstractResourceFrameworkTest.findResource("schema/i18nSchema_v1.xsd");
        AbstractResourceFrameworkTest.injectValue(xmlHandler, "resourceSchema", new Resource[]{schema});
        List<II18nResourceHandler> resourceHandlers = new ArrayList<>();
        resourceHandlers.add(xmlHandler);
        return resourceHandlers;
    }

}
