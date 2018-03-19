/**
 *
 */
package de.taimos.dvalin.i18n;

/**
 * @author psigloch
 *
 */
public interface II18nCallback {

	/**
	 * @param label text key
	 * @param locale locale
	 * @param value value
	 */
	void addText(String label, String locale, String value);

    /**
     * @param element the i18n element
     */
    void addText(I18nElement element);

}