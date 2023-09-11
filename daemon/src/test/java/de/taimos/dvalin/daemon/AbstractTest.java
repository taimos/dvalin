package de.taimos.dvalin.daemon;

import de.taimos.daemon.log4j.Log4jLoggingConfigurer;
import de.taimos.daemon.spring.RunnerConfiguration;

/**
 * Copyright 2023 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
@RunnerConfiguration(config = TestConfig.class, svc = "svc", loggingConfigurer = Log4jLoggingConfigurer.class)
public class AbstractTest {

    final String getDemo() {
        return System.getProperty("demo");
    }
}
