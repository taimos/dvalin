package de.taimos.dvalin.interconnect.model.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.taimos.dvalin.interconnect.model.ivo.IVO;

/**
 * A receiver method:<br>
 * - MUST have a single parameter extending {@link IVO}<br>
 * - MUST NOT have a return value<br>
 * - MUST NOT throw an Exception<br>
 * It is used for something like fire and forget scenarios.
 *
 * @see Daemon
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DaemonReceiverMethod {

	/**
	 * Requests must be send in secure mode (encrypted communication). Otherwise a request is rejected by the daemon.
	 */
	boolean secure() default false;

	/**
	 * The method can be executed multiple times and always produces the same result. (if the call fails and the method is idempotent the
	 * call will be retried several times)
	 */
	boolean idempotent();

}
