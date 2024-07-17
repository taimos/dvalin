package de.taimos.dvalin.interconnect.core.daemon.jms;

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

import de.taimos.dvalin.interconnect.core.daemon.IDaemonMessageHandler;
import de.taimos.dvalin.interconnect.core.daemon.exceptions.IdemponentRetryException;
import de.taimos.dvalin.jms.DvalinConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.lang.NonNull;
import org.springframework.util.ErrorHandler;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.function.Function;


/**
 * Listen to JMS messages for this daemon.
 *
 * @author thoeger/psigloch
 */
public final class DaemonMessageListener extends DefaultMessageListenerContainer
    implements MessageListener, ErrorHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final IDaemonMessageHandler handler;

    /**
     * @param jmsFactory          JMS factory
     * @param concurrentConsumers to use for this message listener
     * @param handlerCreator      create for {@link IDaemonMessageHandler}
     * @param destination         for the message listener
     */
    public DaemonMessageListener(DvalinConnectionFactory jmsFactory, String concurrentConsumers, Function<Logger, IDaemonMessageHandler> handlerCreator, Destination destination) {
        super();
        this.handler = handlerCreator.apply(this.logger);
        this.setConnectionFactory(jmsFactory);
        this.setErrorHandler(this);
        this.setConcurrency(concurrentConsumers);
        this.setDestination(destination);
        this.setMessageListener(this);
    }

    @Override
    public void onMessage(final Message message) {
        try {
            this.handler.onMessage(message);
        } catch (final IdemponentRetryException e) {
            // we are in non-transactional wonderland but the method is idempotent so we throw the message into spring and redeliver the
            // message or send to DLQ!
            throw e;
        } catch (final Exception e) {
            // we are in non-transactional wonderland so we catch the exception which leads to a request without a response.
            this.logger.error("Exception", e);
        }
    }

    @Override
    public void handleError(@NonNull final Throwable e) {
        // this method is called by spring if onMessage throws a RuntimeException (this means redevlivery, this means this exception will
        // maybe logged several times or end in DLQ!)
        this.logger.warn("Exception, retry!", e);
    }

}
