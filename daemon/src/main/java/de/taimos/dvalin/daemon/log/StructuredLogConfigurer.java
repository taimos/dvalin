package de.taimos.dvalin.daemon.log;

/*-
 * #%L
 * Daemon support for dvalin
 * %%
 * Copyright (C) 2015 - 2017 Taimos GmbH
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

import de.taimos.daemon.DaemonProperties;
import de.taimos.daemon.log4j.Log4jDaemonProperties;
import de.taimos.daemon.log4j.Log4jLoggingConfigurer;

/**
 * @deprecated use Log4jLoggingConfigurer with JSON layout
 */
@Deprecated
public class StructuredLogConfigurer {

    public static void setup() {
        System.setProperty(Log4jDaemonProperties.LOGGER_LAYOUT, Log4jDaemonProperties.LOGGER_LAYOUT_JSON);
        System.setProperty(DaemonProperties.LOGGER_CONFIGURER, Log4jLoggingConfigurer.class.getCanonicalName());
    }
}
