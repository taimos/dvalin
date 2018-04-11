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

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.net.SyslogAppender;

/**
 * @author hoegertn
 *
 */
public final class Log4jDaemonProperties {

    private Log4jDaemonProperties() {
        //
    }

    /** the logger level (see {@link Level}) */
	public static final String LOGGER_LEVEL = "logger.level";
	/** the logger pattern */
    public static final String LOGGER_PATTERN = "logger.pattern";

	/** the logger layout */
    public static final String LOGGER_LAYOUT = "logger.layout";
	/** the logger layout - Pattern */
    public static final String LOGGER_LAYOUT_PATTERN = "pattern";
	/** the logger layout - JSON */
    public static final String LOGGER_LAYOUT_JSON = "json";

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

}
