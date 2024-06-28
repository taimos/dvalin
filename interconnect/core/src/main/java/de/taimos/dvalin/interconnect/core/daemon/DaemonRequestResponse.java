package de.taimos.dvalin.interconnect.core.daemon;

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

import de.taimos.dvalin.interconnect.core.model.DvalinInterconnectJmsSendObject;
import de.taimos.dvalin.interconnect.core.model.DvalinInterconnectJmsSendObject.DvalinInterconnectJmsSendObjectBuilder;
import de.taimos.dvalin.interconnect.model.FutureImpl;
import de.taimos.dvalin.interconnect.model.InterconnectList;
import de.taimos.dvalin.interconnect.model.InterconnectMapper;
import de.taimos.dvalin.interconnect.model.InterconnectObject;
import de.taimos.dvalin.interconnect.model.ivo.daemon.DaemonErrorIVO;
import de.taimos.dvalin.interconnect.model.service.ADaemonErrorNumber;
import de.taimos.dvalin.interconnect.model.service.DaemonError;
import de.taimos.dvalin.interconnect.model.service.DaemonErrorNumber;
import de.taimos.dvalin.interconnect.model.service.DaemonScanner;
import de.taimos.dvalin.jms.IJmsConnector;
import de.taimos.dvalin.jms.exceptions.InfrastructureException;
import de.taimos.dvalin.jms.exceptions.MessageCryptoException;
import de.taimos.dvalin.jms.model.DvalinJmsResponseObject;
import de.taimos.dvalin.jms.model.JmsTarget;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.zip.DataFormatException;

public final class DaemonRequestResponse implements IDaemonRequestResponse {

    private static final long DEFAULT_TIMEOUT = 10;

    private static final TimeUnit DEFAULT_TIMEOUT_UNIT = TimeUnit.SECONDS;

    private final Executor executor = Executors.newCachedThreadPool();

    private final IJmsConnector jmsConnector;

    @Autowired
    public DaemonRequestResponse(IJmsConnector jmsConnector) {
        this.jmsConnector = jmsConnector;
    }

    private static final class GenericError extends ADaemonErrorNumber {

        private static final long serialVersionUID = 1L;

        public GenericError(int aNumber, String aDaemon) {
            super(aNumber, aDaemon);
        }

    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <R> R toResponse(final InterconnectObject ico, final Class<R> responseClazz) throws DataFormatException, DaemonError {
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
        } else if (responseClazz.isAssignableFrom(ico.getClass())) {
            return (R) ico;
        }
        throw new DataFormatException("Response was not of type " + responseClazz.getSimpleName());
    }

    @Override
    public <R> R sync(final UUID uuid, final String queue, final InterconnectObject request, final Class<R> responseClazz) throws ExecutionException {
        return this.sync(uuid, queue, request, responseClazz, DaemonRequestResponse.DEFAULT_TIMEOUT,
            DaemonRequestResponse.DEFAULT_TIMEOUT_UNIT);
    }

    @Override
    public <R> R sync(final UUID uuid, final String queue, final InterconnectObject request, final Class<R> responseClazz, final long timeout, final TimeUnit unit) throws ExecutionException {
        return this.sync(uuid, queue, request, responseClazz, timeout, unit, false);
    }

    @Override
    public <R> R sync(final UUID uuid, final String queue, final InterconnectObject request, final Class<R> responseClazz, final long timeout, final TimeUnit unit, final boolean secure) throws ExecutionException {
        try {
            InterconnectObject ico = this.request(uuid, queue, request, timeout, unit, secure);
            return DaemonRequestResponse.toResponse(ico, responseClazz);
        } catch (final Exception e) {
            throw new ExecutionException(e);
        }
    }

    @Override
    public <R> Future<R> async(final UUID uuid, final String queue, InterconnectObject request, Class<R> responseClazz) {
        return this.async(uuid, queue, request, responseClazz, DaemonRequestResponse.DEFAULT_TIMEOUT,
            DaemonRequestResponse.DEFAULT_TIMEOUT_UNIT);
    }

    @Override
    public <R> Future<R> async(final UUID uuid, final String queue, final InterconnectObject request, final Class<R> responseClazz, final long timeout, final TimeUnit unit) {
        return this.async(uuid, queue, request, responseClazz, timeout, unit, false);
    }

    @Override
    public <R> Future<R> async(final UUID uuid, final String queue, final InterconnectObject request, final Class<R> responseClazz, final long timeout, final TimeUnit unit, final boolean secure) {
        final FutureImpl<R> f = new FutureImpl<>();
        this.executor.execute(() -> {
            try {
                InterconnectObject ico = this.request(uuid, queue, request, timeout, unit, secure);
                f.set(DaemonRequestResponse.toResponse(ico, responseClazz));
            } catch (final Exception e) {
                f.set(e);
            }

        });

        return f;
    }

    private static InterconnectObject castToInterconnectObject(TextMessage textMessage) throws InfrastructureException {
        InterconnectObject ico;
        try {
            ico = InterconnectMapper.fromJson(textMessage.getText());
        } catch (JMSException | IOException var2) {
            throw new InfrastructureException("Failed to read message");
        }
        return ico;
    }

    private InterconnectObject request(UUID uuid, String queue, InterconnectObject request, long timeout, TimeUnit unit, boolean secure) throws InfrastructureException, MessageCryptoException {
        DvalinInterconnectJmsSendObject requestObject = (DvalinInterconnectJmsSendObject) new DvalinInterconnectJmsSendObjectBuilder() //
            .withUuid(uuid) //
            .withRequestICO(request) //
            .withDestinationName(queue) //
            .withTarget(JmsTarget.QUEUE) //
            .withSecure(secure) //
            .withReceiveTimeout(TimeUnit.MILLISECONDS.convert(timeout, unit)) //
            .withSendTimeout(TimeUnit.MILLISECONDS.convert(timeout, unit)) //
            .withPriority(IJmsConnector.MSGPRIORITY) //
            .build();
        DvalinJmsResponseObject responseObject = this.jmsConnector.request(requestObject);
        return DaemonRequestResponse.castToInterconnectObject(responseObject.getTextMessage());
    }
}
