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

import de.taimos.dvalin.interconnect.model.InterconnectContext;
import de.taimos.dvalin.interconnect.model.InterconnectObject;
import de.taimos.dvalin.interconnect.model.ivo.daemon.VoidIVO;
import de.taimos.dvalin.interconnect.model.service.Daemon;
import de.taimos.dvalin.interconnect.model.service.DaemonError;
import de.taimos.dvalin.interconnect.model.service.DaemonScanner;
import de.taimos.dvalin.interconnect.model.service.IDaemon;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Copyright 2015 Taimos GmbH<br>
 * <br>
 *
 * @author Thorsten Hoeger
 */
public abstract class ADaemonProxyFactory implements IDaemonProxyFactory {

    /**
     * @param uuid   Universally unique identifier of the request
     * @param queue  Queue name
     * @param ico    InterconnectObject
     * @param secure (encrypted communication)
     * @throws Exception If something went wrong
     */
    protected abstract void sendToQueue(UUID uuid, String queue, InterconnectObject ico, boolean secure) throws Exception;

    /**
     * @param uuid          Universally unique identifier of the request
     * @param queue         Queue name
     * @param request       Request IVO
     * @param responseClazz the response class
     * @param timeout       maximum time to wait
     * @param unit          time unit of the timeout argument
     * @param secure        Secure (encrypted communication)
     * @param <R>           Response type
     * @return Response IVO
     * @throws ExecutionException If the requests fails
     */
    protected abstract <R> R syncRequest(UUID uuid, String queue, InterconnectObject request, Class<R> responseClazz, long timeout, TimeUnit unit, boolean secure) throws ExecutionException;

    @Override
    public final <D extends IDaemon> D create(final Class<D> daemon) {
        final String queueName;
        if (daemon.isAnnotationPresent(Daemon.class)) {
            queueName = daemon.getAnnotation(Daemon.class).name() + ".request";
        } else {
            throw new IllegalArgumentException("Daemon interface has no @Daemon annotation");
        }

        return (D) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[]{daemon}, new InvocationHandler() {

            @Override
            public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                try {
                    final DaemonScanner.DaemonMethod dm = DaemonScanner.scan(method);
                    if (dm.getType() == DaemonScanner.Type.voit) {
                        ADaemonProxyFactory.this.sendToQueue(InterconnectContext.getUuid(), queueName, (InterconnectObject) args[0], dm.isSecure());
                        return null;
                    }
                    final Class<?> responseClass;
                    if (method.getReturnType().equals(Void.TYPE)) {
                        responseClass = VoidIVO.class;
                    } else {
                        responseClass = method.getReturnType();
                    }
                    return ADaemonProxyFactory.this.syncRequest(InterconnectContext.getUuid(), queueName, (InterconnectObject) args[0], responseClass, dm.getTimeoutInMs(), TimeUnit.MILLISECONDS, dm.isSecure());
                } catch (final ExecutionException e) {
                    if (e.getCause() instanceof DaemonError) {
                        throw e.getCause();
                    }
                    if (e.getCause() != null) {
                        throw e.getCause();
                    }
                    throw e;
                }
            }
        });
    }
}
