package de.taimos.dvalin.interconnect.core.spring;

import de.taimos.dvalin.interconnect.core.daemon.ADaemonMessageHandler;
import de.taimos.dvalin.interconnect.core.daemon.DaemonMethodRegistry.RegistryEntry;
import de.taimos.dvalin.interconnect.core.daemon.DaemonResponse;
import de.taimos.dvalin.interconnect.model.service.ADaemonHandler;
import de.taimos.dvalin.interconnect.model.service.IDaemonHandler;
import org.slf4j.Logger;
import org.springframework.beans.factory.BeanFactory;

/**
 *  Copyright 2022 Taimos GmbH<br>
 * <br>
 *
 * @author psigloch
 */
public class SingleDaemonMessageHandler extends ADaemonMessageHandler {

    private final Logger logger;

    private final IDaemonMessageSender messageSender;

    private final BeanFactory beanFactory;


    /**
     * @param aLogger        the logger
     * @param aHandlerClazz  the handler clazz
     * @param aMessageSender the message sender
     * @param beanFactory    the bean factory
     */
    public SingleDaemonMessageHandler(final Logger aLogger, final Class<? extends ADaemonHandler> aHandlerClazz, final IDaemonMessageSender aMessageSender, BeanFactory beanFactory) {
        super(aHandlerClazz, false);
        this.logger = aLogger;
        this.messageSender = aMessageSender;
        this.beanFactory = beanFactory;
    }

    @Override
    protected void reply(final DaemonResponse response, final boolean secure) throws Exception {
        this.messageSender.reply(response, secure);
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
