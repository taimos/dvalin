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
public final class DaemonProperties {

    private DaemonProperties() {
    }

    /** daemon startup mode (dev|run) */
	public static final String STARTUP_MODE = "startupMode";
	/** daemon startup mode - Development */
    public static final String STARTUP_MODE_DEV = "dev";
	/** daemon startup mode - Run (Foreground) */
    public static final String STARTUP_MODE_RUN = "run";

	/** the ttl for dns timeouts */
    public static final String DNS_TTL = "dns.ttl";

	/** the clazz of the {@link ILoggingConfigurer} */
    public static final String LOGGER_CONFIGURER = "loggerConfigurer";

	/** the name of the daemon */
    public static final String DAEMON_NAME = "daemonName";
	/** the name of the daemon */
    public static final String SERVICE_NAME = "serviceName";

	/** the property source type (aws, c2, file, http) */
    public static final String PROPERTY_SOURCE = "property.source";
	/** the property location (file name or URL) */
    public static final String PROPERTY_LOCATION = "property.location";
	/** the CloudConductor server URL if property.source=c2 */
    public static final String PROPERTY_SERVER = "property.server";
	/** the CloudConductor template if property.source=c2 */
    public static final String PROPERTY_TEMPLATE = "property.template";

	/** the property source type - Amazon Web Services UserData */
    public static final String PROPERTY_SOURCE_AWS = "aws";
	/** the property source type - local file */
    public static final String PROPERTY_SOURCE_FILE = "file";
	/** the property source type - HTTP GET resource */
    public static final String PROPERTY_SOURCE_HTTP = "http";
	/** the property source type - Cinovo CloudConductor Server */
    public static final String PROPERTY_SOURCE_C2 = "c2";

}
