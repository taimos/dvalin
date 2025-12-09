package de.taimos.dvalin.interconnect.core.daemon.handler;

import de.taimos.dvalin.interconnect.core.daemon.IDaemonMessageHandler;
import de.taimos.dvalin.interconnect.core.daemon.IDaemonMessageHandlerFactory;
import de.taimos.dvalin.interconnect.core.daemon.IDaemonMessageSender;
import de.taimos.dvalin.interconnect.core.daemon.RequestHandler;
import de.taimos.dvalin.interconnect.model.InterconnectObject;
import de.taimos.dvalin.interconnect.model.service.ADaemonHandler;
import de.taimos.dvalin.interconnect.model.service.IDaemonHandler;
import de.taimos.dvalin.jms.crypto.ICryptoService;
import org.slf4j.Logger;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
public class DefaultMessageHandlerFactory implements IDaemonMessageHandlerFactory {
    private final ApplicationContext applicationContext;
    private final IDaemonMessageSender messageSender;
    private final ICryptoService cryptoService;
    private final MessageHandlerType requestHandlerMode;
    private final List<Function<InterconnectObject, String>> additionalLogAppender = new ArrayList<>();
    private Logger loggerOverride;

    /**
     * @param applicationContext spring application context
     * @param messageSender      to use for the message handler
     * @param cryptoService      for encryption and decryption of messages
     * @param requestHandlerMode mode of the request handler: "multi" has special handling
     * @deprecated use the constructor with the enum mode for requestHandler mode.
     */
    @Deprecated
    public DefaultMessageHandlerFactory(ApplicationContext applicationContext, IDaemonMessageSender messageSender, ICryptoService cryptoService, String requestHandlerMode) {
        this.applicationContext = applicationContext;
        this.messageSender = messageSender;
        this.cryptoService = cryptoService;
        this.requestHandlerMode = MessageHandlerType.from(requestHandlerMode);
    }

    /**
     * @param applicationContext spring application context
     * @param messageSender      to use for the message handler
     * @param cryptoService      for encryption and decryption of messages
     * @param requestHandlerMode mode of the request handler
     */
    public DefaultMessageHandlerFactory(ApplicationContext applicationContext, IDaemonMessageSender messageSender, ICryptoService cryptoService,
        MessageHandlerType requestHandlerMode) {
        this.applicationContext = applicationContext;
        this.messageSender = messageSender;
        this.cryptoService = cryptoService;
        this.requestHandlerMode = requestHandlerMode;
    }

    /**
     * @param logger the logger to use, instead of the default one
     */
    @SuppressWarnings("unused")
    public void defaultLoggerOverride(Logger logger) {
        this.loggerOverride = logger;
    }

    /**
     * @param appender the appender to add to this message handler
     */
    @SuppressWarnings("unused")
    public void addAdditionalLogAppender(Function<InterconnectObject, String> appender) {
        this.additionalLogAppender.add(appender);
    }

    public IDaemonMessageHandler create(Logger logger) {
        AutowireCapableBeanFactory beanFactory = this.applicationContext.getAutowireCapableBeanFactory();
        if (Objects.requireNonNull(this.requestHandlerMode) == MessageHandlerType.MULTI) {
            return this.createMultiDaemonMessageHandler(this.loggerOverride == null ? logger : this.loggerOverride, beanFactory);
        }
        return this.createSingleDaemonMessageHandler(this.loggerOverride == null ? logger : this.loggerOverride, beanFactory);
    }

    private SingleDaemonMessageHandler createSingleDaemonMessageHandler(Logger logger, AutowireCapableBeanFactory beanFactory) {
        final ADaemonHandler rh = (ADaemonHandler) beanFactory.getBean("requestHandler");
        return new SingleDaemonMessageHandler(logger, rh.getClass(), this.cryptoService, this.messageSender, beanFactory, this.additionalLogAppender);
    }

    private MultiDaemonMessageHandler createMultiDaemonMessageHandler(Logger logger, AutowireCapableBeanFactory beanFactory) {
        Set<Class<? extends IDaemonHandler>> handlers = new HashSet<>();
        for (Object o : this.applicationContext.getBeansWithAnnotation(RequestHandler.class).values()) {
            if (o instanceof IDaemonHandler) {
                handlers.add(((IDaemonHandler) o).getClass());
            }
        }
        return new MultiDaemonMessageHandler(logger, handlers, this.cryptoService, this.messageSender, beanFactory, this.additionalLogAppender);
    }
}
