package de.taimos.dvalin.interconnect.core.config;

import de.taimos.dvalin.interconnect.core.daemon.DaemonRequestResponse;
import de.taimos.dvalin.interconnect.core.daemon.IDaemonRequestResponse;
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


}
