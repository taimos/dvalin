package de.taimos.dvalin.interconnect.core.spring;

/*
 * #%L
 * Dvalin interconnect core library
 * %%
 * Copyright (C) 2016 Taimos GmbH
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

import javax.annotation.PostConstruct;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ErrorHandler;

import de.taimos.daemon.spring.annotations.ProdComponent;
import de.taimos.dvalin.interconnect.core.daemon.ADaemonMessageHandler;
import de.taimos.dvalin.interconnect.core.daemon.DaemonResponse;
import de.taimos.dvalin.interconnect.core.daemon.IdemponentRetryException;
import de.taimos.dvalin.interconnect.model.service.ADaemonHandler;
import de.taimos.dvalin.interconnect.model.service.IDaemonHandler;


/**
 * Listen to JMS messages for this daemon.
 */
@ProdComponent("messageListener")
public final class DaemonMessageListener implements MessageListener, ErrorHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IDaemonMessageSender messageSender;

    @Autowired
    private BeanFactory beanFactory;

    private DaemonMessageHandler handler;


    private static final class DaemonMessageHandler extends ADaemonMessageHandler {

        private final Logger logger;

        private final IDaemonMessageSender messageSender;

        private BeanFactory beanFactory;


        public DaemonMessageHandler(final Logger aLogger, final Class<? extends ADaemonHandler> aHandlerClazz, final IDaemonMessageSender aMessageSender, BeanFactory beanFactory) {
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
        protected IDaemonHandler createRequestHandler() {
            return (ADaemonHandler) this.beanFactory.getBean("requestHandler");
        }

        @Override
        protected Logger getLogger() {
            return this.logger;
        }
    }


    /** */
    public DaemonMessageListener() {
        super();

    }

    /** */
    @PostConstruct
    public void start() {
        final ADaemonHandler rh = (ADaemonHandler) this.beanFactory.getBean("requestHandler");
        this.handler = new DaemonMessageHandler(this.logger, rh.getClass(), this.messageSender, this.beanFactory);
    }

    @Override
    public void onMessage(final Message message) {
        try {
            this.handler.onMessage(message);
        } catch (final IdemponentRetryException e) {
            // we are in non transactional wonderland but the method is idempotent so we throw the message into spring and redeliver the
            // message or send to DLQ!
            throw e;
        } catch (final Exception e) {
            // we are in non transactional wonderland so we catch the exception which leads to a request without a response.
            this.logger.error("Exception", e);
        }
    }

    @Override
    public void handleError(final Throwable e) {
        // this method is called by spring if onMessage throws a RuntimeException (this means redevlivery, this means this exception will
        // maybe logged several times or end in DLQ!)
        this.logger.warn("Exception, retry!", e);
    }

}
