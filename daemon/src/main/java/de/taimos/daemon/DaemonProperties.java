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

/**
 * @author hoegertn
 *
 */
public interface DaemonProperties {

	/** daemon startup mode (dev|run) */
	String STARTUP_MODE = "startupMode";
	/** daemon startup mode - Development */
	String STARTUP_MODE_DEV = "dev";
	/** daemon startup mode - Run (Foreground) */
	String STARTUP_MODE_RUN = "run";

	/** the ttl for dns timeouts */
	String DNS_TTL = "dns.ttl";

	/** the clazz of the {@link ILoggingConfigurer} */
	String LOGGER_CONFIGURER = "loggerConfigurer";

	/** the name of the daemon */
	String DAEMON_NAME = "daemonName";
	/** the name of the daemon */
	String SERVICE_NAME = "serviceName";

	/** the property source type (aws, c2, file, http) */
	String PROPERTY_SOURCE = "property.source";
	/** the property location (file name or URL) */
	String PROPERTY_LOCATION = "property.location";
	/** the CloudConductor server URL if property.source=c2 */
	String PROPERTY_SERVER = "property.server";
	/** the CloudConductor template if property.source=c2 */
	String PROPERTY_TEMPLATE = "property.template";

	/** the property source type - Amazon Web Services UserData */
	String PROPERTY_SOURCE_AWS = "aws";
	/** the property source type - local file */
	String PROPERTY_SOURCE_FILE = "file";
	/** the property source type - HTTP GET resource */
	String PROPERTY_SOURCE_HTTP = "http";
	/** the property source type - Cinovo CloudConductor Server */
	String PROPERTY_SOURCE_C2 = "c2";
}
