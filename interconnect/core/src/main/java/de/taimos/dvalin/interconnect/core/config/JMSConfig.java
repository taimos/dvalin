package de.taimos.dvalin.interconnect.core.config;

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

import de.taimos.dvalin.interconnect.core.daemon.DaemonRequestResponse;
import de.taimos.dvalin.interconnect.core.daemon.IDaemonRequestResponse;
import de.taimos.dvalin.interconnect.core.event.EventMessageListener;
import de.taimos.dvalin.interconnect.core.spring.DaemonMessageListener;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.jms.ConnectionFactory;
import java.util.Collection;
import java.util.HashSet;


@Configuration
@Profile(de.taimos.daemon.spring.Configuration.PROFILES_PRODUCTION)
@EnableTransactionManagement
public class JMSConfig {

    @Value("${interconnect.jms.broker}")
    private String brokerUrl;

    @Value("${interconnect.jms.consumers:2-8}")
    private String consumers;

    @Value("${serviceName}")
    private String serviceName;

    @Value("${interconnect.jms.virtualtopic.prefix:VirtualTopic}")
    private String virtualTopicPrefix;
    @Value("${interconnect.jms.virtualtopic.consumerprefix:Consumer}")
    private String consumerPrefix;


    @Bean
    public ConnectionFactory jmsConnectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL(this.brokerUrl);
        return factory;
    }


    @Bean(destroyMethod = "stop")
    public PooledConnectionFactory jmsFactory(ConnectionFactory jmsConnectionFactory) {
        PooledConnectionFactory pool = new PooledConnectionFactory();
        pool.setConnectionFactory(jmsConnectionFactory);
        pool.setCreateConnectionOnStartup(true);
        pool.setIdleTimeout(0);
        pool.setMaxConnections(3);
        pool.setMaximumActiveSessionPerConnection(100);
        pool.setBlockIfSessionPoolIsFull(false);
        return pool;
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
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public IDaemonRequestResponse requestResponse() {
        return new DaemonRequestResponse();
    }


    @Bean
    public DefaultMessageListenerContainer jmsListenerContainer(PooledConnectionFactory jmsFactory, DaemonMessageListener messageListener) {
        DefaultMessageListenerContainer dmlc = new DefaultMessageListenerContainer();
        dmlc.setConnectionFactory(jmsFactory);
        dmlc.setErrorHandler(messageListener);
        dmlc.setConcurrency(this.consumers);

        dmlc.setDestination(new ActiveMQQueue(this.serviceName + ".request"));
        dmlc.setMessageListener(messageListener);
        return dmlc;
    }

    @Bean
    public Collection<DefaultMessageListenerContainer> jmsEventListeners(PooledConnectionFactory jmsFactory, EventMessageListener eventMessageListener) {
        Collection<DefaultMessageListenerContainer> result = new HashSet<>();
        if(eventMessageListener == null) {
            return result;
        }

        for(String domain : eventMessageListener.getDomains()) {
            ActiveMQQueue virtualTopic = new ActiveMQQueue(this.consumerPrefix + "." + this.serviceName + "." + this.virtualTopicPrefix + "." + domain);

            DefaultMessageListenerContainer dmlc = new DefaultMessageListenerContainer();
            dmlc.setConnectionFactory(jmsFactory);
            dmlc.setErrorHandler(eventMessageListener);
            dmlc.setConcurrency(this.consumers);
            dmlc.setDestination(virtualTopic);
            dmlc.setMessageListener(eventMessageListener);
            result.add(dmlc);
        }
        return result;
    }


}
