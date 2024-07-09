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

import de.taimos.dvalin.interconnect.core.daemon.IDaemonMessageSender;
import de.taimos.dvalin.interconnect.core.daemon.exceptions.FrameworkErrors;
import de.taimos.dvalin.interconnect.core.daemon.exceptions.FrameworkErrors.GenericError;
import de.taimos.dvalin.interconnect.core.daemon.exceptions.UnexpectedTypeException;
import de.taimos.dvalin.interconnect.core.daemon.model.InterconnectContext;
import de.taimos.dvalin.interconnect.core.daemon.util.DaemonExceptionMapper;
import de.taimos.dvalin.interconnect.model.FutureImpl;
import de.taimos.dvalin.interconnect.model.InterconnectList;
import de.taimos.dvalin.interconnect.model.InterconnectMapper;
import de.taimos.dvalin.interconnect.model.InterconnectObject;
import de.taimos.dvalin.interconnect.model.ivo.daemon.DaemonErrorIVO;
import de.taimos.dvalin.interconnect.model.service.DaemonError;
import de.taimos.dvalin.interconnect.model.service.DaemonErrorNumber;
import de.taimos.dvalin.interconnect.model.service.DaemonScanner;
import de.taimos.dvalin.jms.IJmsConnector;
import de.taimos.dvalin.jms.exceptions.CreationException;
import de.taimos.dvalin.jms.exceptions.CreationException.Source;
import de.taimos.dvalin.jms.exceptions.InfrastructureException;
import de.taimos.dvalin.jms.exceptions.SerializationException;
import de.taimos.dvalin.jms.exceptions.TimeoutException;
import de.taimos.dvalin.jms.model.JmsResponseContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * JMS Message sender.
 *
 * @author fzwirn
 */
@Component
public final class InterconnectMessageSender implements IDaemonMessageSender {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Executor executor = Executors.newCachedThreadPool();

    private final IJmsConnector jmsConnector;
    private final int tempQueueRetry;

    /**
     * @param jmsConnector   JMS connector to use
     * @param tempQueueRetry the deloy used before a retry
     */
    @Autowired
    public InterconnectMessageSender(IJmsConnector jmsConnector, @Value("${interconnect.tempqueue.retry:100}") int tempQueueRetry) {
        this.jmsConnector = jmsConnector;
        this.tempQueueRetry = tempQueueRetry;
    }

    @Override
    public void sendRequest(InterconnectContext interconnectContext) throws DaemonError, TimeoutException {
        this.logger.debug("TextMessage send: {}", interconnectContext.getBody());
        try {
            this.jmsConnector.send(interconnectContext);
        } catch (InfrastructureException e) {
            if (this.checkForRetry(interconnectContext, e)) {
                this.sendRequestRetry(interconnectContext);
                return;
            }
            DaemonExceptionMapper.mapAndThrow(e);
        } catch (SerializationException e) {
            DaemonExceptionMapper.mapAndThrow(e);
        }
    }

    private boolean checkForRetry(InterconnectContext so, InfrastructureException e) throws TimeoutException {
        if (!so.isIdempotent()) {
            return false;
        }
        if (!(e instanceof CreationException || e instanceof TimeoutException)) {
            return false;
        }
        return !(e instanceof CreationException) ||
               Source.DESTINATION.equals(((CreationException) e).getExceptionSource());
    }

    private void sendRequestRetry(InterconnectContext so) throws DaemonError, TimeoutException {
        try {
            this.logger.warn("Retrying message send to {} after {}ms", so.getDestinationName(), this.tempQueueRetry);
            Thread.sleep(this.tempQueueRetry);
            this.jmsConnector.send(so);
        } catch (InfrastructureException | SerializationException ex) {
            DaemonExceptionMapper.mapAndThrow(ex);
        } catch (InterruptedException ex) {
            this.logger.error("Interrupted while sending a retry message to {}", so.getDestinationName(), ex);
        }
    }

    private JmsResponseContext<? extends Message> sendSyncRequestRetry(InterconnectContext so) throws DaemonError, TimeoutException {
        try {
            this.logger.warn("Retrying message send to {} after {}ms", so.getDestinationName(), this.tempQueueRetry);
            Thread.sleep(this.tempQueueRetry);
            return this.jmsConnector.request(so);
        } catch (InfrastructureException | SerializationException ex) {
            throw new DaemonError(FrameworkErrors.RETRY_FAILED_ERROR, DaemonExceptionMapper.map(ex));
        } catch (InterruptedException ex) {
            throw new DaemonError(FrameworkErrors.RETRY_FAILED_ERROR, ex);
        }
    }

    @Override
    public <R> R syncRequest(InterconnectContext interconnectContext, Class<R> responseClazz) throws DaemonError, TimeoutException {
        try {
            return this.request(interconnectContext, responseClazz);
        } catch (final Exception e) {
            DaemonExceptionMapper.mapAndThrow(e);
            return null;
        }
    }

    @Override
    public <R> Future<R> asyncRequest(InterconnectContext interconnectContext, Class<R> responseClazz) {
        final FutureImpl<R> f = new FutureImpl<>();
        this.executor.execute(() -> {
            try {
                R value = this.request(interconnectContext, responseClazz);
                f.set(value);
            } catch (final Exception e) {
                f.set(DaemonExceptionMapper.map(e));
            }
        });
        return f;
    }

    private <R> R request(InterconnectContext requestObject, Class<R> responseClazz) throws DaemonError, InfrastructureException, SerializationException, UnexpectedTypeException {
        JmsResponseContext<? extends Message> responseObject = null;
        try {
            responseObject = this.jmsConnector.request(requestObject);
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        } catch (InfrastructureException e) {
            if (this.checkForRetry(requestObject, e)) {
                responseObject = this.sendSyncRequestRetry(requestObject);
            }
        }

        InterconnectObject ico = null;
        if (responseObject != null && responseObject.getReceivedMessage() instanceof TextMessage) {
            TextMessage receivedMessage = (TextMessage) responseObject.getReceivedMessage();
            try {
                ico = InterconnectMapper.fromJson(receivedMessage.getText());
            } catch (IOException e) {
                throw new SerializationException("Failed to read ico from received message.", e);
            } catch (JMSException e) {
                throw new InfrastructureException("Failed to read text of text message", e);
            }
        }
        return InterconnectMessageSender.castToResponseClass(ico, responseClazz);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static <R> R castToResponseClass(final InterconnectObject ico, final Class<R> responseClazz) throws DaemonError, UnexpectedTypeException {
        if (ico instanceof DaemonErrorIVO) {
            final DaemonErrorIVO de = (DaemonErrorIVO) ico;
            final DaemonErrorNumber den = new GenericError(de.getNumber(), de.getDaemon());
            throw new DaemonError(den, ((DaemonErrorIVO) ico).getMessage());
        }
        if (responseClazz.isArray() && (ico instanceof InterconnectList)) {
            final InterconnectList list = (InterconnectList) ico;
            final Object obj = Array.newInstance(responseClazz.getComponentType(), list.getElements().size());
            return (R) list.getElements().toArray(DaemonScanner.object2Array(responseClazz.getComponentType(), obj));
        } else if ((ico instanceof InterconnectList) && List.class.isAssignableFrom(responseClazz)) {
            final InterconnectList list = (InterconnectList) ico;
            return (R) list.getElements();
        } else if (ico != null && responseClazz.isAssignableFrom(ico.getClass())) {
            return (R) ico;
        }
        throw new UnexpectedTypeException("Response was not of type " + responseClazz.getSimpleName());
    }
}
