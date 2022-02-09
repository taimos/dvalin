package de.taimos.daemon.log4j;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter.Result;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.ConsoleAppender.Target;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.SyslogAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Order;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.net.Protocol;

import java.net.URI;

/**
 * Copyright 2022 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Order(50)
@Plugin(name = "DvalinLog4jConfigurationFactory", category = ConfigurationFactory.CATEGORY)
public class DvalinLog4jConfigurationFactory extends ConfigurationFactory {

    private static final String CONSOLE_NAME = "CONSOLE";
    private static final String DAROFI_NAME = "DAROFI";
    private static final String SYSLOG_NAME = "SYSLOG";

    /**
     * Create minimal default configuration
     *
     * @param builder   config builder
     * @return default configuration
     */
    public static Configuration configure(ConfigurationBuilder<?> builder) {
        LayoutComponentBuilder layout = createDefaultLayout(builder);
        return configure(builder, layout, true, null, null, null, null, null, null);
    }

    /**
     * Create config with custom log file
     *
     * @param builder           config builder
     * @param layout            layout builder
     * @param consoleEnabled    log to console
     * @param fileName          log file name
     * @param filePattern       log file pattern
     * @return built configuration
     */
    public static Configuration configure(ConfigurationBuilder<?> builder, LayoutComponentBuilder layout, boolean consoleEnabled, String fileName, String filePattern) {
        return configure(builder, layout, consoleEnabled, fileName, filePattern, null, null, null, null);
    }

    /**
     * programmatic log4j2 configuration, see https://www.baeldung.com/log4j2-programmatic-config
     *
     * @param builder           config builder
     * @param layout            layout builder
     * @param consoleEnabled    log to console
     * @param syslogHost        syslog host
     * @param syslogFacility    syslog facility
     * @param syslogLayout      syslog layout
     * @return built configuration
     */
    public static Configuration configure(ConfigurationBuilder<?> builder, LayoutComponentBuilder layout, boolean consoleEnabled,
                                          String fileName, String filePattern,
                                          String syslogHost, String syslogFacility, LayoutComponentBuilder syslogLayout, Level syslogLevel) {
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

        if (syslogHost != null && syslogFacility != null && syslogLayout != null) {
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
    public static AppenderComponentBuilder createConsoleAppender(ConfigurationBuilder<?> builder) {
        return builder.newAppender(CONSOLE_NAME, "Console").addAttribute("target", Target.SYSTEM_OUT);
    }

    /**
     * @param builder       config builder
     * @param fileName      log file name
     * @param filePattern   log file pattern
     * @return builder for {@link RollingFileAppender}
     */
    public static AppenderComponentBuilder createRollingFileAppender(ConfigurationBuilder<?> builder, String fileName, String filePattern) {
        return builder.newAppender(DAROFI_NAME, "RollingFile").addAttribute("fileName", fileName).addAttribute("filePattern", filePattern);
    }

    /**
     * @param daemonName    the daemon name
     * @return log file path as String
     */
    public static String getLogFilePath(String daemonName) {
        String name = (daemonName != null && !daemonName.isEmpty()) ? daemonName : "daemon";
        return "log/" + name + ".log";
    }

    public static String getLogFilePattern(String filePath) {
        return filePath + ".%d{yyyy-MM-dd}";
    }

    /**
     * @param builder   config builder
     * @param host      syslog host
     * @param facility  syslog facility
     * @return builder for {@link SyslogAppender}
     */
    public static AppenderComponentBuilder createSyslogAppender(ConfigurationBuilder<?> builder, String host, String facility) {
        return builder.newAppender(SYSLOG_NAME, "Syslog") //
            .addAttribute("protocol", Protocol.UDP) //
            .addAttribute("port", 514) //
            .addAttribute("host", host) //
            .addAttribute("facility", facility);
    }

    /**
     * @param builder   config builder
     * @return builder for default {@link PatternLayout}
     */
    public static LayoutComponentBuilder createDefaultLayout(ConfigurationBuilder<?> builder) {
        return builder.newLayout("PatternLayout").addAttribute("pattern", Log4jDaemonProperties.DEFAULT_PATTERN);
    }

    /**
     * @param builder   config builder
     * @return builder for default {@link PatternLayout}
     */
    public static LayoutComponentBuilder createSyslogLayout(ConfigurationBuilder<?> builder, String daemonName) {
        return builder.newLayout("PatternLayout").addAttribute("pattern", daemonName + ": %-5p %c %x - %m%n");
    }

    /**
     * @param builder   config builder
     * @param pattern   string pattern to use
     * @return builder for {@link PatternLayout}
     */
    public static LayoutComponentBuilder createLayout(ConfigurationBuilder<?> builder, String pattern) {
        return builder.newLayout("PatternLayout").addAttribute("pattern", pattern);
    }

    @Override
    public Configuration getConfiguration(LoggerContext loggerContext, ConfigurationSource source) {
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        return configure(builder);
    }

    @Override
    public Configuration getConfiguration(LoggerContext loggerContext, String name, URI configLocation) {
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        builder.addProperty("name", name);
        return configure(builder);
    }

    @Override
    public String[] getSupportedTypes() {
        return new String[] {"*"};
    }
}
