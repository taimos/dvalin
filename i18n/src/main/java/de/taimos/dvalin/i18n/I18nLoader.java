/**
 *
 */
package de.taimos.dvalin.i18n;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author aeichel/psigloch
 */
@Component
public class I18nLoader implements II18nCallback, II18nAccess {

    private static final Logger LOGGER = LoggerFactory.getLogger(I18nLoader.class);
    private static final Map<String, Map<String, String>> stringMap = new HashMap<>();

    @Value("${i18n.locale.default:en}")
    private String DEFAULT_LOCALE_STRING;
    private Locale DEFAULT_LOCALE;

    @Autowired
    private List<II18nResourceHandler> resourceHandlers;

    @PostConstruct
    private void initializeResources() {
        this.DEFAULT_LOCALE = Locale.forLanguageTag(this.DEFAULT_LOCALE_STRING);
        for(II18nResourceHandler resourceHandler : this.resourceHandlers) {
            resourceHandler.initializeResources(this);
        }
    }

    @Override
    public void addText(String label, String locale, String value) {
        Map<String, String> entry = I18nLoader.stringMap.computeIfAbsent(locale, k -> new HashMap<>());
        entry.put(label, value);
    }

    @Override
    public void addText(Map<String, Map<String, String>> elements) {
        for (Entry<String, Map<String, String>> languageEntry : elements.entrySet()) {
            String language = languageEntry.getKey();
            Map<String, String> additionalResourcesMap = languageEntry.getValue();

            Map<String, String> existingResourcesMap = I18nLoader.stringMap.get(language);
            if (existingResourcesMap != null) {
                existingResourcesMap.putAll(additionalResourcesMap);
            } else {
                I18nLoader.stringMap.put(language, new HashMap<>(additionalResourcesMap));
            }
        }
    }

    @Override
    public String getString(String identifier) {
        return this.getString(null, identifier);
    }

    @Override
    public String getString(Locale locale, String identifier) {
        String theResult = null;
        Locale usedLocal = locale == null ? this.DEFAULT_LOCALE : locale;
        Map<String, String> table = I18nLoader.stringMap.get(usedLocal.getLanguage());
        if(table != null) {
            theResult = table.get(identifier);
        }
        if(theResult == null) {
            if(!usedLocal.equals(this.DEFAULT_LOCALE)) {
                return this.getString(this.DEFAULT_LOCALE, identifier);
            }
            I18nLoader.LOGGER.error("Did not find text key '{}'", identifier);
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