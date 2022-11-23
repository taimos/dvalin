package de.taimos.daemon.log4j;

/*
 * #%L
 * Daemon Library Log4j extension
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

import de.taimos.daemon.DaemonStarter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.appender.SyslogAppender;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author hoegertn
 *
 */
public final class Log4jDaemonProperties {

    private Log4jDaemonProperties() {
        //
    }

    /** the default {@link Level} **/
    public static final Level DEFAULT_LEVEL = Level.INFO;
    /** the default pattern */
    public static final String DEFAULT_PATTERN = "%date{HH:mm:ss,SSS} %-5level %logger{36} %equals{%mdc}{{}}{} - %message%n";

    /** the logger level (see {@link Level}) */
	public static final String LOGGER_LEVEL = "logger.level";

    /** the logger layout */
    public static final String LOGGER_LAYOUT = "logger.layout";
	/** the logger layout - Pattern */
    public static final String LOGGER_LAYOUT_PATTERN = "pattern";
	/** the logger layout - JSON */
    public static final String LOGGER_LAYOUT_JSON = "json";

    /** true to use {@link ConsoleAppender}; false to disable */
    public static final String LOGGER_STDOUT = "logger.stdout";
	/** true to use {@link FileAppender}; false to disable */
    public static final String LOGGER_FILE = "logger.file";
	/** true to use {@link SyslogAppender}; false to disable */
    public static final String LOGGER_SYSLOG = "logger.syslog";

	/** the log level for syslog (see {@link Level}) */
    public static final String SYSLOG_LEVEL = "syslog.level";
	/** the syslog facility (LOCAL0, LOCAL1, ...) */
    public static final String SYSLOG_FACILITY = "syslog.facility";
	/** the host for remote syslog */
    public static final String SYSLOG_HOST = "syslog.host";

    public static final String ADDITIONAL_LOGGING = "logger.additional";


    public static Map<String, Level> getCustomLevelMap() {
        String config = DaemonStarter.getDaemonProperties().getProperty(Log4jDaemonProperties.ADDITIONAL_LOGGING);
        Level defaultLevel = Level.toLevel(DaemonStarter.getDaemonProperties().getProperty(Log4jDaemonProperties.LOGGER_LEVEL), Level.INFO);
        return getCustomLevelMap(config, defaultLevel);
    }

    public static Map<String, Level> getCustomLevelMap(String configString, Level defaultLevel) {
        if (configString == null || configString.isEmpty()) {
            return new HashMap<>();
        }

        Pattern configSeparator = Pattern.compile(";");
        Pattern kvSeparator = Pattern.compile("=");

        Map<String, Level> map = new HashMap<>();
        for (String config : configSeparator.split(configString)) {
            String[] kvParts  = kvSeparator.split(config);
            if (kvParts.length == 2) {
                String name = kvParts[0].trim();
                Level level = Level.toLevel(kvParts[1].trim(), defaultLevel);
                if (!name.isEmpty() && level != defaultLevel) {
                    map.put(name, level);
                }
            }
        }
        return map;
    }
}
