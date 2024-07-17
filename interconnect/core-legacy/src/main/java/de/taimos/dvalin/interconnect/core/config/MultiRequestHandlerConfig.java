package de.taimos.dvalin.interconnect.core.config;

import de.taimos.daemon.spring.conditional.OnSystemProperty;
import de.taimos.dvalin.interconnect.core.spring.IDaemonMessageHandlerFactory;
import de.taimos.dvalin.interconnect.core.spring.IDaemonMessageSender;
import de.taimos.dvalin.interconnect.core.spring.MultiDaemonMessageHandler;
import de.taimos.dvalin.interconnect.core.spring.RequestHandler;
import de.taimos.dvalin.interconnect.model.service.IDaemonHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.HashSet;
import java.util.Set;

/**
 * Copyright 2022 Taimos GmbH<br>
 * <br>
 *
 * @author psigloch
 */
@Configuration
@OnSystemProperty(propertyName = "interconnect.requesthandler.mode", propertyValue = "multi")
public class MultiRequestHandlerConfig {

    @Bean
    @Primary
    public IDaemonMessageHandlerFactory createDaemonMessageHandlerFactory(ApplicationContext applicationContext, IDaemonMessageSender messageSender) {
        return logger -> {
            Set<Class<? extends IDaemonHandler>> handlers = new HashSet<>();
            for (Object o : applicationContext.getBeansWithAnnotation(RequestHandler.class).values()) {
                if (o instanceof IDaemonHandler) {
                    handlers.add(((IDaemonHandler) o).getClass());
                }
            }
            return new MultiDaemonMessageHandler(logger, handlers, messageSender, applicationContext.getAutowireCapableBeanFactory());
        };
    }
}
