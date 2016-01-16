package de.taimos.dvalin.interconnect.model.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import de.taimos.dvalin.interconnect.model.ivo.IVO;

/**
 * A request method:<br>
 * - MUST have a single parameter extending {@link IVO}<br>
 * - MUST have a return value of:<br>
 * * extending {@link IVO}<br>
 * * void (an VoidIVO is send as response)<br>
 * * List<? extends {@link IVO}><br>
 * * extending {@link IVO}[]<br>
 * * MUST throw an {@link DaemonError}<br>
 * It is used for something like request/response scenarios.
 *
 * @see Daemon
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DaemonRequestMethod {

	/**
	 * The maximum amount of time the requester have to wait for a response from the daemon.<br>
	 * After the timeout is reached the requester will not receive a response from the daemon any more.<br>
	 * ATTENTION: The request can be executed successfully although the timeout was reached and no response was delivered!<br>
	 */
	long timeout() default 10;

	/**
	 * The time unit of the timeout parameter.
	 */
	TimeUnit timeoutUnit() default TimeUnit.SECONDS;

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
