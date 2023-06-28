package de.taimos.dvalin.daemon;

import de.taimos.daemon.log4j.Log4jLoggingConfigurer;
import de.taimos.daemon.spring.SpringDaemonTestRunner;
import de.taimos.daemon.spring.SpringDaemonTestRunner.AdditionalRunnerConfiguration;
import de.taimos.daemon.spring.SpringDaemonTestRunner.RunnerConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;

import static org.junit.Assert.assertEquals;

/**
 * Copyright 2023 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
@RunWith(SpringDaemonTestRunner.class)
@RunnerConfiguration(config = DvalinTestRunnerConfig.class, loggingConfigurer = Log4jLoggingConfigurer.class, svc = "AdditionalConfigTest")
@AdditionalRunnerConfiguration(config = {AdditionalTestConfig.class})
public class AdditionalConfigTest {

    @Value("${jwtauth.issuer}")
    private String jwtIssuer;

    /**
     * Test if the property from {@link AdditionalTestConfig} was set up.
     */
    @Test
    public void testAdditionalConfiguration() {
        assertEquals("The propery 'jwtauth.issuer' was not equals to the value of AdditionalTestConfig.TEST_VALUE",
            AdditionalTestConfig.TEST_VALUE, this.jwtIssuer);
    }
}
