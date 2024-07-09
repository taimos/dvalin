package de.taimos.dvalin.interconnect.core.daemon.handler;

import de.taimos.dvalin.interconnect.core.daemon.IDaemonMessageSender;
import de.taimos.dvalin.interconnect.core.daemon.util.DaemonMethodRegistry.RegistryEntry;
import de.taimos.dvalin.interconnect.model.service.ADaemonHandler;
import de.taimos.dvalin.interconnect.model.service.IDaemonHandler;
import de.taimos.dvalin.jms.crypto.ICryptoService;
import org.slf4j.Logger;
import org.springframework.beans.factory.BeanFactory;

import java.util.Collections;

/**
 * Copyright 2022 Taimos GmbH<br>
 * <br>
 *
 * @author psigloch
 */
public class SingleDaemonMessageHandler extends ADaemonMessageHandler {

    private final Logger logger;

    private final BeanFactory beanFactory;


    /**
     * @param aLogger        the logger
     * @param aHandlerClazz  the handler clazz
     * @param cryptoService  the message acrype service
     * @param aMessageSender the message sender
     * @param beanFactory    the bean factory
     */
    public SingleDaemonMessageHandler(final Logger aLogger, final Class<? extends ADaemonHandler> aHandlerClazz, final ICryptoService cryptoService, final IDaemonMessageSender aMessageSender, BeanFactory beanFactory) {
        super(Collections.singleton(aHandlerClazz), aMessageSender, cryptoService, false);
        this.logger = aLogger;
        this.beanFactory = beanFactory;
    }

    @Override
    protected IDaemonHandler createRequestHandler(RegistryEntry registryEntry) {
        return (ADaemonHandler) this.beanFactory.getBean("requestHandler");
    }

    @Override
    protected Logger getLogger() {
        return this.logger;
    }
}
