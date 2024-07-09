package de.taimos.dvalin.interconnect.core.daemon.handler;

import de.taimos.dvalin.interconnect.core.daemon.IDaemonMessageSender;
import de.taimos.dvalin.interconnect.core.daemon.util.DaemonMethodRegistry.RegistryEntry;
import de.taimos.dvalin.interconnect.model.service.IDaemonHandler;
import de.taimos.dvalin.jms.crypto.ICryptoService;
import org.slf4j.Logger;
import org.springframework.beans.factory.BeanFactory;

import java.util.Collection;

/**
 * Copyright 2022 Taimos GmbH<br>
 * <br>
 *
 * @author psigloch
 */
public class MultiDaemonMessageHandler extends ADaemonMessageHandler {

    private final Logger logger;


    private final BeanFactory beanFactory;


    /**
     * @param aLogger         the logger
     * @param aHandlerClazzes the handler classes
     * @param cryptoService   the message crypt service
     * @param aMessageSender  the message sender
     * @param beanFactory     the bean factory
     */
    public MultiDaemonMessageHandler(final Logger aLogger, final Collection<Class<? extends IDaemonHandler>> aHandlerClazzes, final ICryptoService cryptoService, final IDaemonMessageSender aMessageSender, BeanFactory beanFactory) {
        super(aHandlerClazzes, aMessageSender, cryptoService, false);
        this.logger = aLogger;
        this.beanFactory = beanFactory;
    }

    @Override
    protected IDaemonHandler createRequestHandler(RegistryEntry registryEntry) {
        return this.beanFactory.getBean(registryEntry.getHandlerClazz());
    }

    @Override
    protected Logger getLogger() {
        return this.logger;
    }
}
