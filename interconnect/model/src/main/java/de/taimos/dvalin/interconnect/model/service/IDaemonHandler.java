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

import java.util.UUID;

import de.taimos.dvalin.interconnect.model.ivo.IVO;

/**
 * @see ADaemonHandler
 */
public interface IDaemonHandler extends IDaemon {

	/**
	 * @return Context of the request
	 */
	IContext getContext();

	/**
	 * If a RuntimeException occurs during the request you have the chance to handle it here. The idea is to handle RuntimeExceptions by
	 * throwing a DaemonError.
	 *
	 * @param exception RuntimeException
	 * @throws DaemonError DaemonError
	 */
	void exceptionHook(RuntimeException exception) throws DaemonError;

	/**
	 * For every request this method is called. Keep in mind that the DaemonHandler instance is the same as the one serving the request.
	 */
	void beforeRequestHook();

	/**
	 * After every request this method is called. Keep in mind that the DaemonHandler instance is the same as the one serving the request.
	 */
	void afterRequestHook();


	interface IContext {

		/**
		 * @return Request class
		 */
		Class<? extends IVO> requestClass();

		/**
		 * @return Universally unique identifier of the request
		 */
		UUID uuid();

		/**
		 * @return Number of attempts to send the message (start at 1)
		 */
		int deliveryCount();

		/**
		 * @return True if the message is being resent to the daemon
		 */
		boolean redelivered();

	}
}
