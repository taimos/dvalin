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
import de.taimos.dvalin.jms.IJmsConnector;
import de.taimos.dvalin.jms.activemq.ActiveMQPooledConnectionFactory;
import de.taimos.dvalin.jms.activemq.ActiveMqCryptoService;
import de.taimos.dvalin.jms.activemq.ActiveMqJmsConnector;
import de.taimos.dvalin.jms.activemq.DvalinActiveMqConnectionFactory;
import de.taimos.dvalin.jms.crypto.ICryptoService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.jms.Destination;


@Configuration
@Profile(de.taimos.daemon.spring.Configuration.PROFILES_PRODUCTION)
@EnableTransactionManagement
public class JMSConfig {

    @Value("${interconnect.jms.broker}")
    private String brokerUrl;

    @Value("${interconnect.jms.userName:#{null}}")
    private String userName;

    @Value("${interconnect.jms.password:}")
    private String password;

    @Value("${serviceName}")
    private String serviceName;

    @Bean(name = "DvalinConnectionFactory")
    public DvalinConnectionFactory jmsConnectionFactory() {
        return new DvalinActiveMqConnectionFactory(this.brokerUrl, this.userName, this.password);
    }

    @Bean(name = "DvalinPooledConnectionFactory", destroyMethod = "stop")
    public PooledConnectionFactory jmsPooledConnectionFactory(DvalinConnectionFactory jmsConnectionFactory) {
        return new ActiveMQPooledConnectionFactory().initDefault(jmsConnectionFactory);
    }

    @Bean
    public IJmsConnector jmsConnector(@Qualifier("DvalinPooledConnectionFactory") PooledConnectionFactory jmsConnectionFactory, ICryptoService cryptoService) {
        return new ActiveMqJmsConnector(jmsConnectionFactory, cryptoService);
    }

    @Bean
    public ICryptoService cryptoService() {
        return new ActiveMqCryptoService();
    }

    @Bean
    public JmsTemplate jmsTemplate(PooledConnectionFactory jmsFactory) {
        return new JmsTemplate(jmsFactory);
    }


    @Bean
    public JmsTemplate topicJmsTemplate(PooledConnectionFactory jmsFactory) {
        JmsTemplate template = new JmsTemplate(jmsFactory);
        template.setPubSubDomain(true);
        return template;
    }

    @Bean
    public Destination serviceRequestQueue() {
        return new ActiveMQQueue(this.serviceName + ".request");
    }
}
