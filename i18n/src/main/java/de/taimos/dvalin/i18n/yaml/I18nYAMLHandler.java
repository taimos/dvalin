package de.taimos.dvalin.i18n.yaml;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.taimos.dvalin.i18n.I18nElement;
import de.taimos.dvalin.i18n.II18nCallback;
import de.taimos.dvalin.i18n.II18nResourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * Copyright 2018 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component
public class I18nYAMLHandler implements II18nResourceHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("classpath*:resources/*.yaml")
    private Resource[] resourceFiles;

    private YAMLFactory factory = new YAMLFactory();
    private ObjectMapper mapper = new ObjectMapper();

    public void initializeResources(II18nCallback callback) {
        if(this.resourceFiles != null) {
            for(Resource file : this.resourceFiles) {
                this.loadResourceFile(file, callback);
            }
        }
    }

    private void loadResourceFile(Resource file, II18nCallback callback) {
        try(InputStream inParser = file.getInputStream()) {
            MappingIterator<I18nElement> elements = this.mapper.readValues(this.factory.createParser(inParser), I18nElement.class);
            while(elements.hasNext()) {
                callback.addText(elements.next());
            }
        } catch(final Exception e) {
            this.logger.error(e.getMessage(), e);

        }
    }


}
