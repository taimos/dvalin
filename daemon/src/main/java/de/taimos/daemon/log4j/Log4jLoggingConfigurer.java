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

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.net.SyslogAppender;

import de.taimos.daemon.DaemonProperties;
import de.taimos.daemon.DaemonStarter;
import de.taimos.daemon.ILoggingConfigurer;

public class Log4jLoggingConfigurer implements ILoggingConfigurer {

    private static final String DEFAULT_PATTERN = "%d{HH:mm:ss,SSS} %-5p %c %x - %m%n";
    private static final String FALSE_STRING = "false";
    private final Logger rlog = Logger.getRootLogger();

    private SyslogAppender syslog;
    private DailyRollingFileAppender darofi;
    private ConsoleAppender console;

    @Override
    public void initializeLogging() throws Exception {
        // Clear all existing appenders
        this.rlog.removeAllAppenders();

        this.rlog.setLevel(Level.INFO);

        // CONSOLE is always active
        this.console = new ConsoleAppender();
        this.console.setName("CONSOLE");
        this.console.setLayout(new PatternLayout(DEFAULT_PATTERN));
        this.console.setTarget(ConsoleAppender.SYSTEM_OUT);
        this.console.activateOptions();
        this.rlog.addAppender(this.console);

        // only use SYSLOG and DAROFI in production mode
        if (!DaemonStarter.isDevelopmentMode()) {
            this.darofi = new DailyRollingFileAppender();
            this.darofi.setName("DAROFI");
            this.darofi.setLayout(new PatternLayout(DEFAULT_PATTERN));
            this.darofi.setFile("log/" + DaemonStarter.getDaemonName() + ".log");
            this.darofi.setDatePattern("'.'yyyy-MM-dd");
            this.darofi.setAppend(true);
            this.darofi.setThreshold(Level.INFO);
            this.darofi.activateOptions();
            this.rlog.addAppender(this.darofi);

            this.syslog = new SyslogAppender();
            this.syslog.setName("SYSLOG");
            this.syslog.setLayout(new PatternLayout(DaemonStarter.getDaemonName() + ": %-5p %c %x - %m%n"));
            this.syslog.setSyslogHost("localhost");
            this.syslog.setFacility("LOCAL0");
            this.syslog.setFacilityPrinting(false);
            this.syslog.setThreshold(Level.INFO);
            this.syslog.activateOptions();
            this.rlog.addAppender(this.syslog);
        }
    }

    @Override
    public void reconfigureLogging() throws Exception {
        final Level logLevel = Level.toLevel(DaemonStarter.getDaemonProperties().getProperty(Log4jDaemonProperties.LOGGER_LEVEL), Level.INFO);
        this.rlog.setLevel(logLevel);
        this.rlog.info(String.format("Changed the the log level to %s", logLevel));

        this.console.setLayout(this.getLayout());
        this.console.setThreshold(logLevel);
        this.console.activateOptions();

        if (!DaemonStarter.isDevelopmentMode()) {
            final String fileEnabled = DaemonStarter.getDaemonProperties().getProperty(Log4jDaemonProperties.LOGGER_FILE, FALSE_STRING);
            final String syslogEnabled = DaemonStarter.getDaemonProperties().getProperty(Log4jDaemonProperties.LOGGER_SYSLOG, FALSE_STRING);

            if ((fileEnabled == null) || fileEnabled.equals(FALSE_STRING)) {
                this.rlog.removeAppender(this.darofi);
                this.darofi = null;
                this.rlog.info("Deactivated the FILE Appender");
            } else {
                this.darofi.setThreshold(logLevel);
                this.darofi.setLayout(this.getLayout());
                this.darofi.activateOptions();
            }

            if ((syslogEnabled == null) || syslogEnabled.equals(FALSE_STRING)) {
                this.rlog.removeAppender(this.syslog);
                this.syslog = null;
                this.rlog.info("Deactivated the SYSLOG Appender");
            } else {
                final String host = DaemonStarter.getDaemonProperties().getProperty(Log4jDaemonProperties.SYSLOG_HOST, "localhost");
                final String facility = DaemonStarter.getDaemonProperties().getProperty(Log4jDaemonProperties.SYSLOG_FACILITY, "LOCAL0");
                final Level syslogLevel = Level.toLevel(DaemonStarter.getDaemonProperties().getProperty(Log4jDaemonProperties.SYSLOG_LEVEL), Level.INFO);

                this.syslog.setSyslogHost(host);
                this.syslog.setFacility(facility);
                this.syslog.setThreshold(syslogLevel);
                this.syslog.activateOptions();
                this.rlog.info(String.format("Changed the SYSLOG Appender to host %s and facility %s", host, facility));
            }
        }
    }

    private Layout getLayout() {
        final String logLayout = System.getProperty(Log4jDaemonProperties.LOGGER_LAYOUT, Log4jDaemonProperties.LOGGER_LAYOUT_PATTERN);

        switch (logLayout) {
        case Log4jDaemonProperties.LOGGER_LAYOUT_JSON:
            return new JSONLayout();
        case Log4jDaemonProperties.LOGGER_LAYOUT_PATTERN:
        default:
            final String logPattern = System.getProperty(Log4jDaemonProperties.LOGGER_PATTERN, DEFAULT_PATTERN);
            return new PatternLayout(logPattern);
        }
    }

    @Override
    public void simpleLogging() throws Exception {
        // Clear all existing appenders
        this.rlog.removeAllAppenders();
        final Level logLevel = Level.toLevel(System.getProperty(Log4jDaemonProperties.LOGGER_LEVEL), Level.INFO);
        this.rlog.setLevel(logLevel);

        this.console = new ConsoleAppender();
        this.console.setName("CONSOLE");
        this.console.setLayout(new PatternLayout(DEFAULT_PATTERN));
        this.console.setTarget(ConsoleAppender.SYSTEM_OUT);
        this.console.activateOptions();
        this.rlog.addAppender(this.console);
    }

    public static void setup() {
        System.setProperty(DaemonProperties.LOGGER_CONFIGURER, Log4jLoggingConfigurer.class.getCanonicalName());
    }

}
