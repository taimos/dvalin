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

import de.taimos.daemon.DaemonProperties;
import de.taimos.daemon.DaemonStarter;
import de.taimos.daemon.ILoggingConfigurer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

import java.util.Map;

public class Log4jLoggingConfigurer implements ILoggingConfigurer {

    private static final String FALSE_STRING = "false";

    private final Log4jConfigurationFactory configFactory = new Log4jConfigurationFactory();

    @Override
    public void initializeLogging() {
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();

        String fileName = null;
        String filePattern = null;
        String host = null;
        String facility = null;
        Level syslogLevel = null;
        if (!DaemonStarter.isDevelopmentMode()) {
            fileName = getLogFileName(DaemonStarter.getDaemonName());
            filePattern = getLogFilePattern(fileName);

            host = DaemonStarter.getDaemonProperties().getProperty(Log4jDaemonProperties.SYSLOG_HOST, "localhost");
            facility = DaemonStarter.getDaemonProperties().getProperty(Log4jDaemonProperties.SYSLOG_FACILITY, "LOCAL0");
            syslogLevel = getLevel(Log4jDaemonProperties.SYSLOG_LEVEL, Log4jDaemonProperties.DEFAULT_LEVEL);
        }
        LayoutComponentBuilder layout = this.createConfiguredLayout(builder);
        Configuration config = this.configFactory.configure(builder, layout, true, fileName, filePattern, host, facility, syslogLevel);
        Configurator.reconfigure(config);

        Configurator.setRootLevel(this.getLevel(Log4jDaemonProperties.LOGGER_LEVEL, Log4jDaemonProperties.DEFAULT_LEVEL));
    }

    protected String getLogFileName(String daemonName) {
        String name = (daemonName != null && !daemonName.isEmpty()) ? daemonName : "daemon";
        return "log/" + name + ".log";
    }

    protected String getLogFilePattern(String fileName) {
        return fileName + ".%d{yyyy-MM-dd}";
    }

    @Override
    public void reconfigureLogging() {
        final Logger rlog = LogManager.getRootLogger();
        rlog.info("Reconfigure Logging");

        boolean console = !DaemonStarter.getDaemonProperties().getProperty(Log4jDaemonProperties.LOGGER_STDOUT, "true").equals(FALSE_STRING);

        String fileName = null;
        String filePattern = null;
        String host = null;
        String facility = null;
        Level syslogLevel = null;
        if (!DaemonStarter.isDevelopmentMode()) {
            if (!DaemonStarter.getDaemonProperties().getProperty(Log4jDaemonProperties.LOGGER_FILE, FALSE_STRING).equals(FALSE_STRING)) {
                fileName = getLogFileName(DaemonStarter.getDaemonName());
                filePattern = getLogFilePattern(fileName);
            }

            if (!DaemonStarter.getDaemonProperties().getProperty(Log4jDaemonProperties.LOGGER_SYSLOG, FALSE_STRING).equals(FALSE_STRING)) {
                host = DaemonStarter.getDaemonProperties().getProperty(Log4jDaemonProperties.SYSLOG_HOST, "localhost");
                facility = DaemonStarter.getDaemonProperties().getProperty(Log4jDaemonProperties.SYSLOG_FACILITY, "LOCAL0");
                syslogLevel = getLevel(Log4jDaemonProperties.SYSLOG_LEVEL, Log4jDaemonProperties.DEFAULT_LEVEL);
            }
        }

        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        LayoutComponentBuilder layout = this.createConfiguredLayout(builder);
        Configuration config = this.configFactory.configure(builder, layout, console, fileName, filePattern, host, facility, syslogLevel);
        Configurator.reconfigure(config);

        Level level = this.getLevel(Log4jDaemonProperties.LOGGER_LEVEL, Log4jDaemonProperties.DEFAULT_LEVEL);
        Configurator.setRootLevel(level);
        rlog.info("Set root log level to {}", level);

        Map<String, Level> customLevelMap = Log4jDaemonProperties.getCustomLevelMap();
        if (customLevelMap.isEmpty()) {
            return;
        }
        rlog.info("Set custom log levels");
        Configurator.setLevel(customLevelMap);
    }

    protected Level getLevel(String property, Level defaultLevel) {
        String propertyValue = DaemonStarter.getDaemonProperties().getProperty(property);
        return Level.toLevel(propertyValue, defaultLevel);
    }

    /**
     * @param builder config builder
     * @return layout builder
     */
    protected LayoutComponentBuilder createConfiguredLayout(ConfigurationBuilder<?> builder) {
        switch (System.getProperty(Log4jDaemonProperties.LOGGER_LAYOUT, Log4jDaemonProperties.LOGGER_LAYOUT_PATTERN)) {
            case Log4jDaemonProperties.LOGGER_LAYOUT_JSON:
                return builder.newLayout("JsonTemplateLayout").addAttribute("eventTemplateUri", "classpath:log4j/JsonLogTemplate.json");
            case Log4jDaemonProperties.LOGGER_LAYOUT_PATTERN:
            default:
                return builder.newLayout("PatternLayout").addAttribute("pattern", Log4jDaemonProperties.DEFAULT_PATTERN);
        }
    }

    @Override
    public void simpleLogging() {
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        Configurator.reconfigure(this.configFactory.configure(builder));

        Level level = getLevel(Log4jDaemonProperties.LOGGER_LEVEL, Log4jDaemonProperties.DEFAULT_LEVEL);
        Configurator.setRootLevel(level);
    }

    public static void setup() {
        System.setProperty(DaemonProperties.LOGGER_CONFIGURER, Log4jLoggingConfigurer.class.getCanonicalName());
    }

}
