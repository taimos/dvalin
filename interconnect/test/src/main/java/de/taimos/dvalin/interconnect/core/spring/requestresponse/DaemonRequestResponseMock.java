package de.taimos.dvalin.interconnect.core.spring.requestresponse;

/*
 * #%L
 * Dvalin interconnect test library
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

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;

import de.taimos.daemon.spring.annotations.TestComponent;
import de.taimos.dvalin.interconnect.core.daemon.IDaemonRequestResponse;
import de.taimos.dvalin.interconnect.model.FutureImpl;
import de.taimos.dvalin.interconnect.model.InterconnectObject;

@TestComponent("requestResponse")
public class DaemonRequestResponseMock implements IDaemonRequestResponse {

    private static final long DEFAULT_TIMEOUT = 10;

    private static final TimeUnit DEFAULT_TIMEOUT_UNIT = TimeUnit.SECONDS;

    private final Executor executor = Executors.newCachedThreadPool();

    @Autowired(required = false)
    private IRequestMock requestMock;


    @Override
    public <R> R sync(final UUID uuid, final String queue, final InterconnectObject request, final Class<R> responseClazz) throws ExecutionException {
        return this.sync(uuid, queue, request, responseClazz, DaemonRequestResponseMock.DEFAULT_TIMEOUT, DaemonRequestResponseMock.DEFAULT_TIMEOUT_UNIT);
    }

    @Override
    public <R> R sync(final UUID uuid, final String queue, final InterconnectObject request, final Class<R> responseClazz, final long timeout, final TimeUnit unit) throws ExecutionException {
        try {
            return this.async(uuid, queue, request, responseClazz, timeout, unit).get(timeout, unit);
        } catch (final TimeoutException e) {
            throw new ExecutionException(new de.taimos.dvalin.interconnect.core.exceptions.TimeoutException(timeout));
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ExecutionException(e);
        }
    }

    @Override
    public <R> R sync(UUID uuid, String queue, InterconnectObject request, Class<R> responseClazz, long timeout, TimeUnit unit, boolean secure) throws ExecutionException {
        return this.sync(uuid, queue, request, responseClazz, timeout, unit);
    }

    @Override
    public <R> Future<R> async(final UUID uuid, final String queue, InterconnectObject request, Class<R> responseClazz) {
        return this.async(uuid, queue, request, responseClazz, DaemonRequestResponseMock.DEFAULT_TIMEOUT, DaemonRequestResponseMock.DEFAULT_TIMEOUT_UNIT);
    }

    @Override
    public <R> Future<R> async(final UUID uuid, final String queue, final InterconnectObject request, final Class<R> responseClazz, final long timeout, final TimeUnit unit) {
        if (this.requestMock == null) {
            throw new UnsupportedOperationException("No requestMock");
        }
        final FutureImpl<R> future = new FutureImpl<>();
        final IRequestMock localRequestMock = this.requestMock;
        this.executor.execute(() -> {
            try {
                future.set(localRequestMock.in(uuid, queue, request, responseClazz));
            } catch (final Exception e) {
                future.set(e);
            }
        });
        return future;
    }

    @Override
    public <R> Future<R> async(UUID uuid, String queue, InterconnectObject request, Class<R> responseClazz, long timeout, TimeUnit unit, boolean secure) {
        return this.async(uuid, queue, request, responseClazz, timeout, unit);
    }
}
