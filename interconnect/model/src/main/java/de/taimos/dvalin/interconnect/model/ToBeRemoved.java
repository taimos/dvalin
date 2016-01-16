/**
 *
 */
package de.taimos.dvalin.interconnect.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Deprecation warning. If this annotation is set, the class will not be available after the shown date.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface ToBeRemoved {

	/**
	 * @return the date on that the InterconnectObject / interface will be removed. format as yyyy/mm/dd
	 */
	public String date();
}
