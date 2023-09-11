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

import de.taimos.daemon.spring.annotations.ProdComponent;
import de.taimos.dvalin.interconnect.core.daemon.ADaemonMessageHandler;
import de.taimos.dvalin.interconnect.core.daemon.IdemponentRetryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ErrorHandler;

import jakarta.annotation.PostConstruct;
import javax.jms.Message;
import javax.jms.MessageListener;


/**
 * Listen to JMS messages for this daemon.
 *
 * @author thoeger/psigloch
 */
@ProdComponent("messageListener")
public final class DaemonMessageListener implements MessageListener, ErrorHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IDaemonMessageHandlerFactory messageHandlerFactory;

    private ADaemonMessageHandler handler;

    /**
     *
     */
    public DaemonMessageListener() {
        super();
    }

    /**
     *
     */
    @PostConstruct
    public void start() {
        this.handler = this.messageHandlerFactory.create(this.logger);
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
