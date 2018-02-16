/**
 *
 */
package de.taimos.dvalin.i18n;

/**
 * @author psigloch
 *
 */
interface II18nCallback {

	/**
	 * @param key text key
	 * @param locale locale
	 * @param label label
	 */
	void addText(String key, String locale, String label);

}