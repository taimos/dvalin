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


import java.util.HashMap;
import java.util.Map;

import de.taimos.daemon.properties.CloudConductorPropertyProvider;
import de.taimos.daemon.properties.EmptyPropertyProvider;
import de.taimos.daemon.properties.FilePropertyProvider;
import de.taimos.daemon.properties.IPropertyProvider;
import de.taimos.daemon.properties.SimpleHTTPPropertyProvider;
import de.taimos.daemon.properties.UserDataPropertyProvider;

/**
 * Adapter for {@link IDaemonLifecycleListener}
 *
 * @author hoegertn
 *
 */
public class DaemonLifecycleAdapter implements IDaemonLifecycleListener {

	@Override
	public void doStart() throws Exception {
		// override in subclass when needed
	}

	@Override
	public void doStop() throws Exception {
		// override in subclass when needed
	}

	@Override
	public void started() {
		// override in subclass when needed
	}

	@Override
	public void stopped() {
		// override in subclass when needed
	}

	@Override
	public void stopping() {
		// override in subclass when needed
	}

	@Override
	public void aborting() {
		// override in subclass when needed
	}

	@Override
	public void signalUSR2() {
		// override in subclass when needed
	}

	@Override
	public void exception(final LifecyclePhase phase, final Throwable exception) {
		// override in subclass when needed
		System.err.println("Exception in phase: " + phase.name());
		exception.printStackTrace();
	}

	@Override
	public Map<String, String> loadProperties() {
		Map<String, String> properties = new HashMap<>();
		this.loadBasicProperties(properties);
		properties.putAll(this.getPropertyProvider().loadProperties());
		return properties;
	}

	@Override
	public int getShutdownTimeoutSeconds() {
		return 10;
	}

	/**
	 * Override to set properties to be used in the daemon without specifying them in the {@link IPropertyProvider}
	 *
	 * @param map the map to populate with basic properties
	 */
	@SuppressWarnings("unused")
	protected void loadBasicProperties(Map<String, String> map) {
		// Override if needed
	}

	/**
	 * @return the property provider
	 */
	public IPropertyProvider getPropertyProvider() {
		switch (System.getProperty(DaemonProperties.PROPERTY_SOURCE, "")) {
		case DaemonProperties.PROPERTY_SOURCE_AWS:
			return new UserDataPropertyProvider();
		case DaemonProperties.PROPERTY_SOURCE_FILE:
			return new FilePropertyProvider(System.getProperty(DaemonProperties.PROPERTY_LOCATION));
		case DaemonProperties.PROPERTY_SOURCE_C2:
			return new CloudConductorPropertyProvider(System.getProperty(DaemonProperties.PROPERTY_SERVER), System.getProperty(DaemonProperties.PROPERTY_TEMPLATE));
		case DaemonProperties.PROPERTY_SOURCE_HTTP:
			return new SimpleHTTPPropertyProvider(System.getProperty(DaemonProperties.PROPERTY_LOCATION));
		default:
			return new EmptyPropertyProvider();
		}
	}

}
