package de.taimos.dvalin.interconnect.model.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The @Daemon annotation is used with interfaces that extend {@link IDaemon} to provide information that clients can interact with that
 * daemon.<br>
 * This information are:<br>
 * - The name of the JMS queue ${name}.request<br>
 * - some {@link DaemonRequestMethod}<br>
 * - some {@link DaemonReceiverMethod}<br>
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Daemon {

	/**
	 * @return Daemon name
	 */
	public String name();

}
