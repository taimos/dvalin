package de.taimos.dvalin.i18n;

import de.taimos.dvalin.i18n.yaml.I18nYAMLHandler;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * @author psigloch
 */
public class ResourceFrameworkYAMLTest extends AbstractResourceFrameworkTest {

    @Override
    protected List<II18nResourceHandler> getResourceHandler() {
        I18nYAMLHandler yamlHandler = new I18nYAMLHandler();
        Resource yaml = AbstractResourceFrameworkTest.findResource("i18n/test.yaml");
        AbstractResourceFrameworkTest.injectValue(yamlHandler, "resourceFiles", new Resource[]{yaml});
        List<II18nResourceHandler> resourceHandlers = new ArrayList<>();
        resourceHandlers.add(yamlHandler);
        return resourceHandlers;
    }

}