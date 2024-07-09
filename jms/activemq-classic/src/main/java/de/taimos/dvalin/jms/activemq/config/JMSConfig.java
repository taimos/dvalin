package de.taimos.dvalin.jms.activemq.config;

/*-
 * #%L
 * Dvalin interconnect core library
 * %%
 * Copyright (C) 2016 - 2017 Taimos GmbH
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

import de.taimos.dvalin.jms.DvalinConnectionFactory;
import de.taimos.dvalin.jms.IDestinationService;
import de.taimos.dvalin.jms.IJmsConnector;
import de.taimos.dvalin.jms.JmsConnector;
import de.taimos.dvalin.jms.activemq.ActiveMQPooledConnectionFactory;
import de.taimos.dvalin.jms.activemq.ActiveMqCryptoService;
import de.taimos.dvalin.jms.activemq.ActiveMqDestinationService;
import de.taimos.dvalin.jms.activemq.DvalinActiveMqConnectionFactory;
import de.taimos.dvalin.jms.crypto.ICryptoService;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


/**
 * Default configuration for dvalin-jms with ActiveMQ Classic.
 *
 * @author fzwirn
 */
@Configuration
@Profile(de.taimos.daemon.spring.Configuration.PROFILES_PRODUCTION)
public class JMSConfig {

    @Value("${interconnect.jms.broker}")
    private String brokerUrl;

    @Value("${interconnect.jms.userName:#{null}}")
    private String userName;

    @Value("${interconnect.jms.password:}")
    private String password;

    /**
     * @return a Dvalin JMS connection factory for ActiveMQ Classic
     */
    @Bean(name = "DvalinConnectionFactory", destroyMethod = "stop")
    public DvalinConnectionFactory jmsConnectionFactory() {
        return new DvalinActiveMqConnectionFactory(this.brokerUrl, this.userName, this.password);
    }

    /**
     * @param jmsConnectionFactory the JMS connection factory to be used
     * @return a pooled connection factory for ActiveMQ Classic
     */
    @Bean(name = "DvalinPooledConnectionFactory", destroyMethod = "stop")
    public PooledConnectionFactory jmsPooledConnectionFactory(DvalinConnectionFactory jmsConnectionFactory) {
        return new ActiveMQPooledConnectionFactory().initDefault(jmsConnectionFactory);
    }

    /**
     * @param jmsConnectionFactory the JMS connection factory to be used
     * @param cryptoService        the crypto service
     * @return an instance of {@link IJmsConnector} that uses the {@code jmsConnectionFactory} and the {@code cryptoService}
     */
    @Bean
    public IJmsConnector jmsConnector(@Qualifier("DvalinPooledConnectionFactory") PooledConnectionFactory jmsConnectionFactory, ICryptoService cryptoService) {
        return new JmsConnector(jmsConnectionFactory, cryptoService);
    }

    /**
     * @return a ActiveMQ Classic implementation of {@link ICryptoService}
     */
    @Bean
    public ICryptoService cryptoService() {
        return new ActiveMqCryptoService();
    }

    /**
     * @return a ActiveMQ Classic implementation of {@link IDestinationService}
     */
    @Bean
    public IDestinationService serviceDestination() {
        return new ActiveMqDestinationService();
    }
}
