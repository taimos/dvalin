package de.taimos.daemon.log4j;

import de.taimos.daemon.DaemonStarter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter.Result;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.ConsoleAppender.Target;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.SyslogAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.net.Protocol;

/**
 * Copyright 2022 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
public class Log4jConfigurationFactory {

    private static final String CONSOLE_NAME = "CONSOLE";
    private static final String DAROFI_NAME = "DAROFI";
    private static final String SYSLOG_NAME = "SYSLOG";

    /**
     * Create minimal default configuration
     * @param builder   config builder
     * @return default configuration
     */
    public Configuration configure(ConfigurationBuilder<?> builder) {
        LayoutComponentBuilder layout = createDefaultLayout(builder, Log4jDaemonProperties.DEFAULT_PATTERN);
        return this.configure(builder, layout, true, null, null, null, null, null);
    }

    /**
     * programmatic log4j2 configuration, see https://www.baeldung.com/log4j2-programmatic-config
     *
     * @param builder           config builder
     * @param layout            layout builder
     * @param consoleEnabled    log to console
     * @param fileName          log file name
     * @param filePattern       log file pattern
     * @param syslogHost        syslog host
     * @param syslogFacility    syslog facility
     * @return built configuration
     */
    public Configuration configure(ConfigurationBuilder<?> builder, LayoutComponentBuilder layout, boolean consoleEnabled,
                                      String fileName, String filePattern,
                                      String syslogHost, String syslogFacility, Level syslogLevel) {
        AppenderComponentBuilder console = null;
        AppenderComponentBuilder rolling = null;
        AppenderComponentBuilder syslog = null;

        if (consoleEnabled) {
            console = createConsoleAppender(builder);
            console.add(layout);
            builder.add(console);
        }

        if (fileName != null && filePattern != null) {
            rolling = createRollingFileAppender(builder, fileName, filePattern);
            rolling.addComponent(builder.newComponent("Policies").addComponent(builder.newComponent("TimeBasedTriggeringPolicy")));
            rolling.add(layout);
            builder.add(rolling);
        }

        if (syslogHost != null && syslogFacility != null) {
            LayoutComponentBuilder syslogLayout = createDefaultLayout(builder, DaemonStarter.getDaemonName() + ": %-5p %c %x - %m%n");
            syslog = createSyslogAppender(builder, syslogHost, syslogFacility);
            if (syslogLevel != null) {
                syslog.add(builder.newFilter("ThresholdFilter", Result.ACCEPT, Result.DENY).addAttribute("level", syslogLevel));
            }
            syslog.add(syslogLayout);
            builder.add(syslog);
        }

        // set up root logger
        RootLoggerComponentBuilder rootLogger = builder.newRootLogger(Log4jDaemonProperties.DEFAULT_LEVEL);
        if (console != null) {
            rootLogger.add(builder.newAppenderRef(console.getName()));
        }
        if (rolling != null) {
            rootLogger.add(builder.newAppenderRef(rolling.getName()));
        }
        if (syslog != null) {
            rootLogger.add(builder.newAppenderRef(syslog.getName()));
        }
        builder.add(rootLogger);
        return builder.build();
    }

    /**
     * @param builder   config builder
     * @return builder for {@link ConsoleAppender}
     */
    protected AppenderComponentBuilder createConsoleAppender(ConfigurationBuilder<?> builder) {
        return builder.newAppender(CONSOLE_NAME, "Console").addAttribute("target", Target.SYSTEM_OUT);
    }

    /**
     * @param builder       config builder
     * @param fileName      log file name
     * @param filePattern   log file pattern
     * @return builder for {@link RollingFileAppender}
     */
    protected AppenderComponentBuilder createRollingFileAppender(ConfigurationBuilder<?> builder, String fileName, String filePattern) {
        return builder.newAppender(DAROFI_NAME, "RollingFile").addAttribute("fileName", fileName).addAttribute("filePattern", filePattern);
    }

    /**
     * @param builder   config builder
     * @param host      syslog host
     * @param facility  syslog facility
     * @return builder for {@link SyslogAppender}
     */
    protected AppenderComponentBuilder createSyslogAppender(ConfigurationBuilder<?> builder, String host, String facility) {
        return builder.newAppender(SYSLOG_NAME, "Syslog") //
            .addAttribute("protocol", Protocol.UDP) //
            .addAttribute("port", 514) //
            .addAttribute("host", host) //
            .addAttribute("facility", facility);
    }

    /**
     * @param builder   config builder
     * @param pattern   string pattern to use
     * @return builder for {@link PatternLayout}
     */
    protected LayoutComponentBuilder createDefaultLayout(ConfigurationBuilder<?> builder, String pattern) {
        return builder.newLayout("PatternLayout").addAttribute("pattern", pattern);
    }
}
