package de.taimos.dvalin.interconnect.core.daemon.util;

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

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

import de.taimos.dvalin.interconnect.core.daemon.IDaemonProxyFactory;
import de.taimos.dvalin.interconnect.model.ivo.IPageable;
import de.taimos.dvalin.interconnect.model.ivo.IPageableBuilder;
import de.taimos.dvalin.interconnect.model.ivo.IVO;
import de.taimos.dvalin.interconnect.model.ivo.util.IVOQueryResultIVO_v1;
import de.taimos.dvalin.interconnect.model.service.IDaemon;


/**
 * You can give me a Request-IVO and an IDaemon interface and I will get all the pages for that request as an Iterable if the result is an
 * IVOQueryResultIVO_v1. Be sure to type me according to the return of the Daemon otherwise I will produce a class cast
 * exception at runtime.<br>
 * New Pages are fetched on hasNext() call so be prepared that a call to hasNext() will block all this.limit items for a while.
 *
 * @param <E> IVO type
 * @author thoeger
 */
public final class PagingController<E extends IVO> implements Iterator<E> {

    static {
        try {
            NO_SUCH_METHOD = String.class.getMethod("toString");
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final Method NO_SUCH_METHOD;
    /**
     * Map <Daemon-Interface-Canonical-Name>:<Request-IVO-Canonical-Name> to method
     */
    private static final ConcurrentHashMap<String, Method> METHOD_CACHE = new ConcurrentHashMap<>();

    private final IDaemonProxyFactory proxyFactory;
    private final Class<? extends IDaemon> daemonClass;
    private final int limit;
    private final IPageable initialRequest;
    private int nextOffset = 0;
    private int batchIndex = -1;
    private List<E> batch;


    /**
     * @param proxyFactory   Proxy factory
     * @param daemonClass    Daemon
     * @param limit          Limit
     * @param initialRequest Initial request (offset and limit fields are adjusted!)
     */
    public PagingController(final IDaemonProxyFactory proxyFactory, final Class<? extends IDaemon> daemonClass, final int limit, final IPageable initialRequest) {
        this.proxyFactory = proxyFactory;
        this.daemonClass = daemonClass;
        this.limit = limit;
        this.initialRequest = initialRequest;
    }

    /**
     * @param proxyFactory   Proxy factory
     * @param daemonClass    Daemon
     * @param initialRequest Initial request (offset and limit fields are adjusted!)
     */
    public PagingController(final IDaemonProxyFactory proxyFactory, final Class<? extends IDaemon> daemonClass, final IPageable initialRequest) {
        this(proxyFactory, daemonClass, 100, initialRequest);
    }

    private Method scanProxyMethods(final Object proxy) {
        for (final Method method : proxy.getClass().getDeclaredMethods()) {
            if (method.getParameterTypes().length == 1) {
                if (method.getParameterTypes()[0].equals(this.initialRequest.getClass())) {
                    return method;
                }
            }
        }
        return null;
    }

    private Method extractProxyMethod(final IDaemon proxy) {
        final String cachekey =
            proxy.getClass().getCanonicalName() + ":" + this.initialRequest.getClass().getCanonicalName();
        final Method cached = PagingController.METHOD_CACHE.get(cachekey);
        if (cached != null) {
            if (cached.equals(PagingController.NO_SUCH_METHOD)) {
                throw new RuntimeException("proxy method not found for " + this.initialRequest.getClass());
            }
            return cached;
        }
        final Method method = this.scanProxyMethods(proxy);
        if (method == null) {
            PagingController.METHOD_CACHE.putIfAbsent(cachekey, PagingController.NO_SUCH_METHOD);
            throw new RuntimeException("proxy method not found for " + this.initialRequest.getClass());
        }
        method.setAccessible(true);
        PagingController.METHOD_CACHE.putIfAbsent(cachekey, method);
        return method;
    }

    @Override
    public boolean hasNext() {
        if ((this.batch != null) &&
            (this.batch.isEmpty())) { // we are at the end of the query because we have an empty result
            return false;
        }
        final int nextBatchIndex = this.batchIndex + 1;
        if ((this.batch == null) ||
            (nextBatchIndex == this.batch.size())) { // we are at the end of the batch -> fetch next batch
            if ((this.batch != null) && (this.batch.size() != this.limit)) { // we are at the end of the query
                return false;
            }

            // create request IVO
            final Object request;
            try {
                final IPageableBuilder b = this.initialRequest.createPageableBuilder();
                b.withOffset(this.nextOffset);
                b.withLimit(this.limit);
                request = b.build();
            } catch (final Exception e) {
                throw new RuntimeException("Can not build from IVO", e);
            }

            // execute query
            final IVOQueryResultIVO_v1<E> list;
            try {
                final IDaemon proxy = this.proxyFactory.create(this.daemonClass);
                final Method proxyMethod = this.extractProxyMethod(proxy);
                list = (IVOQueryResultIVO_v1<E>) proxyMethod.invoke(proxy, request);
            } catch (final Exception e) {
                throw new RuntimeException("Can not get next elements", e);
            }

            // update batch
            this.nextOffset += this.limit;
            this.batchIndex = -1;
            this.batch = list.getElements();

            // check if we are at the end of the query
            return !this.batch.isEmpty();
        }
        return true;
    }

    @Override
    public E next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        this.batchIndex++;
        return this.batch.get(this.batchIndex);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
