package de.taimos.daemon;

/*
 * #%L
 * Daemon Library
 * %%
 * Copyright (C) 2012 - 2016 Taimos GmbH
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


import java.util.Map;

/**
 * Listener for the lifecycle of a system daemon
 *
 * @author hoegertn
 *
 */
public interface IDaemonLifecycleListener {

	/**
	 * Will be called to allow for custom startup code
	 *
	 * @throws Exception if start failed
	 */
	void doStart() throws Exception;

	/**
	 * Will be called to allow for custom shutdown code
	 *
	 * @throws Exception if shutdown failed
	 */
	void doStop() throws Exception;

	/**
	 * will be called after successful startup
	 */
	void started();

	/**
	 * will be called after successful shutdown
	 */
	void stopped();

	/**
	 * will be called on imminent shutdown
	 */
	void stopping();

	/**
	 * will be called on imminent abortion
	 */
	void aborting();

	/**
	 * received custom signal SIGUSR2
	 */
	void signalUSR2();

	/**
	 * This method is called if an error occurs. It provides the current {@link LifecyclePhase} and the exception
	 *
	 * @param phase the phase the error occured in
	 * @param exception the occured exception
	 */
	void exception(LifecyclePhase phase, Throwable exception);

	/**
	 * @return the map of properties
	 */
	Map<String, String> loadProperties();

	/**
	 * @return the number of seconds to wait for termination before forcefully stopping the daemon
	 */
	int getShutdownTimeoutSeconds();

}
