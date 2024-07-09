package de.taimos.dvalin.interconnect.core.config;

import de.taimos.dvalin.interconnect.core.EventSender;
import de.taimos.dvalin.interconnect.core.IVORefreshSender;
import de.taimos.dvalin.interconnect.core.daemon.IDaemonMessageHandlerFactory;
import de.taimos.dvalin.interconnect.core.daemon.IDaemonMessageSender;
import de.taimos.dvalin.interconnect.core.daemon.handler.DefaultMessageHandlerFactory;
import de.taimos.dvalin.interconnect.core.daemon.jms.DaemonMessageListener;
import de.taimos.dvalin.jms.DvalinConnectionFactory;
import de.taimos.dvalin.jms.IDestinationService;
import de.taimos.dvalin.jms.IJmsConnector;
import de.taimos.dvalin.jms.crypto.ICryptoService;
import de.taimos.dvalin.jms.model.JmsTarget;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import javax.jms.Destination;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
@Configuration
@Profile(de.taimos.daemon.spring.Configuration.PROFILES_PRODUCTION)
public class InterconnectConfig {

    @Value("${interconnect.jms.consumers:2-8}")
    private String consumers;

    @Value("${serviceName}")
    private String serviceName;

    /**
     * @param applicationContext spring application context
     * @param cryptoService      used for encryption and decryption
     * @param messageSender      for sending messages
     * @param requestHandlerMode mode of the request handler: "multi" has special handling
     * @return a daemon message handler factory
     */
    @Bean
    public IDaemonMessageHandlerFactory createDaemonMessageHandlerFactory(ApplicationContext applicationContext, //
        ICryptoService cryptoService,  //
        IDaemonMessageSender messageSender, //
        @Value("${interconnect.requesthandler.mode:}") String requestHandlerMode) {
        return new DefaultMessageHandlerFactory(applicationContext, messageSender, cryptoService, requestHandlerMode);
    }

    /**
     * @param jmsFactory                      JMS factory
     * @param defaultDaemonRequestDestination destination used for the daemon message listener
     * @param handlerFactory                  message handler factory
     * @return default message listener container
     */
    @Bean(name = "messageListener", destroyMethod = "stop")
    public DefaultMessageListenerContainer jmsListenerContainer(@Qualifier("DvalinConnectionFactory") DvalinConnectionFactory jmsFactory, //
        @Qualifier("defaultDaemonRequestDestination") Destination defaultDaemonRequestDestination, //
        IDaemonMessageHandlerFactory handlerFactory) {
        return new DaemonMessageListener(jmsFactory, this.consumers, handlerFactory::create,
            defaultDaemonRequestDestination);
    }

    /**
     * @param destinationService creates the default daemon request destination
     * @return default daemon request destination
     */
    @Bean(name = "defaultDaemonRequestDestination")
    public Destination getDefaultDaemonRequestDestination(IDestinationService destinationService) {
        return destinationService.createDestination(JmsTarget.QUEUE,
            this.serviceName + ".request");
    }

    /**
     * @param jmsConnector JMS connector to be used
     * @return an instance of a new event sender
     */
    @Bean
    public EventSender eventSender(IJmsConnector jmsConnector) {
        return new EventSender(jmsConnector);
    }

    /**
     * @param jmsConnector JMS connector to be used
     * @return an instance of a new ivo refresh sender
     */
    @Bean
    public IVORefreshSender ivoRefreshSender(IJmsConnector jmsConnector) {
        return new IVORefreshSender(jmsConnector);
    }
}
