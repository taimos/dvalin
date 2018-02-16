/**
 *
 */
package de.taimos.dvalin.i18n;


import java.util.Locale;

/**
 * @author aeichel
 *
 *         Use this interface with @Autowired annotation to get i18n
 */
public interface II18nAccess {

	/**
	 * @param identifier the resource identifier by enum
	 * @return the resolved resource
	 */
	String getString(Enum<?> identifier);

	/**
	 * @param locale the locale
	 * @param identifier the resource identifier by enum
	 * @return the resolved resource
	 */
	String getString(Locale locale, Enum<?> identifier);

	/**
	 * @param identifier the resource identifier
	 * @return the resolved resource
	 */
	String getString(String identifier);

	/**
	 * @param locale the locale
	 * @param identifier the resource identifier
	 * @return the resolved resource
	 */
	String getString(Locale locale, String identifier);

	/**
	 * @param identifier the resource identifier
	 * @param arguments arguments which will be inserted into the string
	 * @return the resolved resource
	 */
	String getString(String identifier, String... arguments);

	/**
	 * @param locale the locale
	 * @param identifier the resource identifier
	 * @param arguments arguments which will be inserted into the string
	 * @return the resolved resource
	 */
	String getString(Locale locale, String identifier, String... arguments);

}