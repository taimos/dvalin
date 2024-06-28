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

import java.lang.reflect.Array;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.zip.DataFormatException;

import de.taimos.dvalin.interconnect.core.InterconnectConnector;
import de.taimos.dvalin.interconnect.core.MessageConnector;
import de.taimos.dvalin.interconnect.model.FutureImpl;
import de.taimos.dvalin.interconnect.model.InterconnectList;
import de.taimos.dvalin.interconnect.model.InterconnectObject;
import de.taimos.dvalin.interconnect.model.ivo.daemon.DaemonErrorIVO;
import de.taimos.dvalin.interconnect.model.service.ADaemonErrorNumber;
import de.taimos.dvalin.interconnect.model.service.DaemonError;
import de.taimos.dvalin.interconnect.model.service.DaemonErrorNumber;
import de.taimos.dvalin.interconnect.model.service.DaemonScanner;

public final class DaemonRequestResponse implements IDaemonRequestResponse {

    private static final long DEFAULT_TIMEOUT = 10;

    private static final TimeUnit DEFAULT_TIMEOUT_UNIT = TimeUnit.SECONDS;

    private final Executor executor = Executors.newCachedThreadPool();


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
        return this.sync(uuid, queue, request, responseClazz, DaemonRequestResponse.DEFAULT_TIMEOUT, DaemonRequestResponse.DEFAULT_TIMEOUT_UNIT);
    }

    @Override
    public <R> R sync(final UUID uuid, final String queue, final InterconnectObject request, final Class<R> responseClazz, final long timeout, final TimeUnit unit) throws ExecutionException {
        return this.sync(uuid, queue, request, responseClazz, timeout, unit, false);
    }

    @Override
    public <R> R sync(final UUID uuid, final String queue, final InterconnectObject request, final Class<R> responseClazz, final long timeout, final TimeUnit unit, final boolean secure) throws ExecutionException {
        try {
            final InterconnectObject response = InterconnectConnector.request(uuid, queue, request, null, secure, TimeUnit.MILLISECONDS.convert(timeout, unit), TimeUnit.MILLISECONDS.convert(timeout, unit), MessageConnector.MSGPRIORITY);
            return DaemonRequestResponse.toResponse(response, responseClazz);
        } catch (final Exception e) {
            throw new ExecutionException(e);
        }
    }

    @Override
    public <R> Future<R> async(final UUID uuid, final String queue, InterconnectObject request, Class<R> responseClazz) {
        return this.async(uuid, queue, request, responseClazz, DaemonRequestResponse.DEFAULT_TIMEOUT, DaemonRequestResponse.DEFAULT_TIMEOUT_UNIT);
    }

    @Override
    public <R> Future<R> async(final UUID uuid, final String queue, final InterconnectObject request, final Class<R> responseClazz, final long timeout, final TimeUnit unit) {
        return this.async(uuid, queue, request, responseClazz, timeout, unit, false);
    }

    @Override
    public <R> Future<R> async(final UUID uuid, final String queue, final InterconnectObject request, final Class<R> responseClazz, final long timeout, final TimeUnit unit, final boolean secure) {
        final FutureImpl<R> f = new FutureImpl<>();
        this.executor.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    final InterconnectObject response = InterconnectConnector.request(uuid, queue, request, null, secure, TimeUnit.MILLISECONDS.convert(timeout, unit), TimeUnit.MILLISECONDS.convert(timeout, unit), MessageConnector.MSGPRIORITY);
                    f.set(DaemonRequestResponse.toResponse(response, responseClazz));
                } catch (final Exception e) {
                    f.set(e);
                }

            }
        });

        return f;
    }
}
