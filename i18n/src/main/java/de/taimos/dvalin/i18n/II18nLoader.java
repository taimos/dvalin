/**
 *
 */
package de.taimos.dvalin.i18n;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

import javax.annotation.PostConstruct;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author aeichel/psigloch
 */
@Component
public class II18nLoader implements II18nCallback, II18nAccess {

	private static final Logger LOGGER = LoggerFactory.getLogger(II18nLoader.class);
	private static final Map<String, Map<String, String>> stringMap = new HashMap<>();

	@Value("${i18n.locale.default:en}")
	private String DEFAULT_LOCALE_STRING;
	private Locale DEFAULT_LOCALE;

	@Value("classpath*:resources/*.xml")
	private Resource[] resourceFiles;

	@Value("classpath*:schema/*i18nSchema*.xsd")
	private Resource[] resourceSchema;

	@PostConstruct
	private void initializeResources() {
		this.DEFAULT_LOCALE = Locale.forLanguageTag(this.DEFAULT_LOCALE_STRING);
		if(this.resourceFiles != null) {
			URL schemaURL = null;

			Integer currentCount = null;
			for(Resource resource : this.resourceSchema) {
				String[] vs = resource.getFilename().split("\\.")[0].split("v");
				Integer count = Integer.valueOf(vs[vs.length - 1]);
				if(currentCount == null || count > currentCount) {
					currentCount = count;
					try {
						schemaURL = resource.getURL();
					} catch(IOException e) {
						II18nLoader.LOGGER.error("Failed to load resource schema.", e);
					}
				}

			}

			for(Resource file : this.resourceFiles) {
				this.loadResourceFile(file, schemaURL);
			}
		}
	}

	private void loadResourceFile(Resource file, URL url) {
		StreamSource inValidator;
		InputStream urlStream = null;
		final String fileName = file.getFilename();
		try(InputStream valstream = file.getInputStream(); InputStream inParser = file.getInputStream()) {
			inValidator = new StreamSource(valstream);
			// New Version with validation.
			// Create a validator for the resourcebundle
			final SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
			final Schema schema = url == null ? schemaFactory.newSchema(new StreamSource(urlStream)) : schemaFactory.newSchema(url);
			final Validator validator = schema.newValidator();
			validator.setErrorHandler(new ErrorHandler() {

				@Override
				public void error(final SAXParseException arg0) {
					II18nLoader.LOGGER.error("XML Error in resource \"" + fileName + "\" on line " + arg0.getLineNumber() + ": " + arg0.getMessage());
				}

				@Override
				public void fatalError(final SAXParseException arg0) {
					II18nLoader.LOGGER.error("XML Fatal Error in resource \"" + fileName + "\" on line " + arg0.getLineNumber() + ": " + arg0.getMessage());
				}

				@Override
				public void warning(final SAXParseException arg0) {
					II18nLoader.LOGGER.error("XML Warning in resource \"" + fileName + "\" on line " + arg0.getLineNumber() + ": " + arg0.getMessage());
				}
			});
			validator.validate(inValidator, null);

			// Parse the resourcebundle
			final SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			factory.setValidating(true);
			factory.setSchema(schema);
			final SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(inParser, new I18nReader(this));

		} catch(final Exception e) {
			II18nLoader.LOGGER.error(e.getMessage(), e);

		}
	}

	@Override
	public void addText(String key, String locale, String label) {
		Map<String, String> entry = II18nLoader.stringMap.computeIfAbsent(locale, k -> new HashMap<>());
		entry.put(key, label);

	}

	@Override
	public String getString(String identifier) {
		return this.getString(null, identifier);
	}

	@Override
	public String getString(Locale locale, String identifier) {
		String theResult = null;
		Locale usedLocal = locale == null ? this.DEFAULT_LOCALE : locale;
		Map<String, String> table = II18nLoader.stringMap.get(usedLocal.getLanguage());
		if(table != null) {
			theResult = table.get(identifier);
		}
		if(theResult == null) {
			if(!usedLocal.equals(this.DEFAULT_LOCALE)) {
				return this.getString(this.DEFAULT_LOCALE, identifier);
			}
			II18nLoader.LOGGER.error("Did not find text key " + identifier);
			return '!' + identifier + '!';
		}
		return theResult;
	}

	@Override
	public String getString(String identifier, String... arguments) {
		return this.getString(null, identifier, arguments);
	}

	@Override
	public String getString(Locale locale, String identifier, String... arguments) {
		String text = this.getString(locale, identifier);
		if(text == null || text.trim().isEmpty()) {
			return text;
		}
		if(arguments == null || arguments.length < 1) {
			return text;
		}

		for(int i = 0; i < arguments.length; i++) {
			if(arguments[i] != null && !arguments[i].isEmpty()) {
				text = text.replace("{" + i + "}", arguments[i]);
			}
		}
		return text;
	}

	@Override
	public String getString(Enum<?> identifier) {
		return this.getString(null, identifier);
	}

	@Override
	public String getString(Locale locale, Enum<?> identifier) {
		final String key = identifier.getClass().getCanonicalName() + "." + identifier.name();
		return this.getString(locale, key);
	}
}