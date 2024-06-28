package de.taimos.dvalin.interconnect.core.config;

import de.taimos.dvalin.interconnect.core.EventSender;
import de.taimos.dvalin.interconnect.core.IVORefreshSender;
import de.taimos.dvalin.interconnect.core.daemon.DaemonRequestResponse;
import de.taimos.dvalin.interconnect.core.daemon.IDaemonRequestResponse;
import de.taimos.dvalin.interconnect.core.spring.DaemonMessageListener;
import de.taimos.dvalin.interconnect.core.spring.IDaemonMessageHandlerFactory;
import de.taimos.dvalin.interconnect.core.spring.IDaemonMessageSender;
import de.taimos.dvalin.interconnect.core.spring.SingleDaemonMessageHandler;
import de.taimos.dvalin.interconnect.model.service.ADaemonHandler;
import de.taimos.dvalin.jms.DvalinConnectionFactory;
import de.taimos.dvalin.jms.IJmsConnector;
import de.taimos.dvalin.jms.crypto.ICryptoService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.jms.Destination;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
@Configuration
@Profile(de.taimos.daemon.spring.Configuration.PROFILES_PRODUCTION)
@EnableTransactionManagement
public class InterconnectConfig {

    @Value("${interconnect.jms.consumers:2-8}")
    private String consumers;


    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public IDaemonRequestResponse requestResponse(IJmsConnector jmsConnector) {
        return new DaemonRequestResponse(jmsConnector);
    }

    @Bean
    public IDaemonMessageHandlerFactory createDaemonMessageHandlerFactory(BeanFactory beanFactory, ICryptoService cryptoService, IDaemonMessageSender messageSender) {
        return logger -> {
            final ADaemonHandler rh = (ADaemonHandler) beanFactory.getBean("requestHandler");
            return new SingleDaemonMessageHandler(logger, rh.getClass(), cryptoService, messageSender, beanFactory);
        };
    }

    @Bean
    public DefaultMessageListenerContainer jmsListenerContainer(@Qualifier("DvalinConnectionFactory") DvalinConnectionFactory jmsFactory, DaemonMessageListener messageListener, Destination serviceRequestQueue) {
        DefaultMessageListenerContainer dmlc = new DefaultMessageListenerContainer();
        dmlc.setConnectionFactory(jmsFactory);
        dmlc.setErrorHandler(messageListener);
        dmlc.setConcurrency(this.consumers);
        dmlc.setDestination(serviceRequestQueue);
        dmlc.setMessageListener(messageListener);
        return dmlc;
    }

    @Bean
    public EventSender eventSender(@Qualifier("DvalinConnectionFactory") DvalinConnectionFactory connectionFactory) {
        return new EventSender(connectionFactory);
    }

    @Bean
    public IVORefreshSender ivoRefreshSender(@Qualifier("DvalinConnectionFactory") DvalinConnectionFactory connectionFactory) {
        return new IVORefreshSender(connectionFactory);
    }
}
