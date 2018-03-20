/**
 *
 */
package de.taimos.dvalin.i18n;

import java.util.Map;

/**
 * @author psigloch
 */
public interface II18nCallback {

    /**
     * @param label  text key
     * @param locale locale
     * @param value  value
     */
    void addText(String label, String locale, String value);

    /**
     * @param elements the i18n elements : key = language; value.key = label; value.value = translation for the language
     */
    void addText(Map<String, Map<String, String>> elements);

}