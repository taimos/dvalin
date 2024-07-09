package de.taimos.dvalin.jms.test.config;

import de.taimos.dvalin.jms.IJmsConnector;
import de.taimos.dvalin.jms.crypto.ICryptoService;
import de.taimos.dvalin.jms.test.JmsConnectorMock;
import de.taimos.dvalin.jms.test.TestCryptoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
@Configuration
@Profile(de.taimos.daemon.spring.Configuration.PROFILES_TEST)
public class JmsTestConfig {


    @Bean
    IJmsConnector jmsConnector() {
        return new JmsConnectorMock();
    }

    @Bean
    ICryptoService cryptoService() {
        return new TestCryptoService();
    }
}
