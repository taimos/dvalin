package de.taimos.dvalin.daemon;

import de.taimos.daemon.log4j.Log4jLoggingConfigurer;
import de.taimos.daemon.spring.AdditionalRunnerConfiguration;
import de.taimos.daemon.spring.RunnerConfiguration;
import de.taimos.daemon.spring.SpringDaemonExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;

/**
 * Copyright 2023 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
@ExtendWith(SpringDaemonExtension.class)
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
        Assertions.assertEquals(AdditionalTestConfig.TEST_VALUE, this.jwtIssuer, "The propery 'jwtauth.issuer' was not equals to the value of AdditionalTestConfig.TEST_VALUE");
    }
}

