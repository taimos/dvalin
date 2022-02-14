package de.taimos.dvalin.i18n.xml;

import de.taimos.dvalin.i18n.II18nCallback;
import de.taimos.dvalin.i18n.II18nResourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Copyright 2018 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
@Component
public class I18nXMLHandler implements II18nResourceHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(I18nXMLHandler.class);

    @Value("classpath*:resources/*.xml")
    private Resource[] resourceFiles;

    @Value("classpath*:schema/*i18nSchema*.xsd")
    private Resource[] resourceSchema;


    public void initializeResources(II18nCallback callback) {
        if (this.resourceFiles != null) {
            URL schemaURL = null;

            Integer currentCount = null;
            for (Resource resource : this.resourceSchema) {
                String[] vs = resource.getFilename().split("\\.")[0].split("v");
                int count = Integer.parseInt(vs[vs.length - 1]);
                if (currentCount == null || count > currentCount) {
                    currentCount = count;
                    try {
                        schemaURL = resource.getURL();
                    } catch (IOException e) {
                        I18nXMLHandler.LOGGER.error("Failed to load resource schema.", e);
                    }
                }

            }

            for (Resource file : this.resourceFiles) {
                this.loadResourceFile(file, schemaURL, callback);
            }
        }
    }

    private void loadResourceFile(Resource file, URL url, II18nCallback callback) {
        StreamSource inValidator;
        InputStream urlStream = null;
        final String fileName = file.getFilename();
        try (InputStream valstream = file.getInputStream(); InputStream inParser = file.getInputStream()) {
            inValidator = new StreamSource(valstream);
            // New Version with validation.
            // Create a validator for the resourcebundle
            final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            final Schema schema = url == null ? schemaFactory.newSchema(new StreamSource(urlStream)) : schemaFactory.newSchema(url);
            final Validator validator = schema.newValidator();
            validator.setErrorHandler(new ErrorHandler() {

                @Override
                public void error(final SAXParseException arg0) {
                    I18nXMLHandler.LOGGER.error("XML Error in resource \"{}\" on line {}: {}", fileName, arg0.getLineNumber(), arg0.getMessage());
                }

                @Override
                public void fatalError(final SAXParseException arg0) {
                    I18nXMLHandler.LOGGER.error("XML Fatal Error in resource \"{}\" on line {}: {}", fileName, arg0.getLineNumber(), arg0.getMessage());
                }

                @Override
                public void warning(final SAXParseException arg0) {
                    I18nXMLHandler.LOGGER.error("XML Warning in resource \"{}\" on line {}: {}", fileName, arg0.getLineNumber(), arg0.getMessage());
                }
            });
            validator.validate(inValidator, null);

            // Parse the resourcebundle
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(true);
            factory.setSchema(schema);
            final SAXParser saxParser = factory.newSAXParser();
            saxParser.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            saxParser.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            saxParser.parse(inParser, new I18nXMLReader(callback));

        } catch (final Exception e) {
            I18nXMLHandler.LOGGER.error(e.getMessage(), e);

        }
    }
}
