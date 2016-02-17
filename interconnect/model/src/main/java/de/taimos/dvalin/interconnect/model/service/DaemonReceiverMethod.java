package de.taimos.dvalin.interconnect.model.service;

/*
 * #%L
 * Dvalin interconnect transfer data model
 * %%
 * Copyright (C) 2016 Taimos GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
     *
     * @return true if secure mode is enabled
	 */
	boolean secure() default false;

	/**
	 * The method can be executed multiple times and always produces the same result. (if the call fails and the method is idempotent the
	 * call will be retried several times)
     *
     * @return true if the call is idempotent
	 */
	boolean idempotent();

}
