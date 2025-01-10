package de.taimos.dvalin.interconnect.core.daemon.proxy;

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

import de.taimos.dvalin.interconnect.core.daemon.IDaemonProxyFactory;
import de.taimos.dvalin.interconnect.core.daemon.model.InterconnectContext;
import de.taimos.dvalin.interconnect.core.daemon.util.DaemonMethodInvocationHandler;
import de.taimos.dvalin.interconnect.model.service.DaemonError;
import de.taimos.dvalin.interconnect.model.service.IDaemon;
import de.taimos.dvalin.interconnect.core.exceptions.TimeoutException;

/**
 * Copyright 2015 Taimos GmbH<br>
 * <br>
 *
 * @author Thorsten Hoeger
 */
public abstract class ADaemonProxyFactory implements IDaemonProxyFactory {


    /**
     * @param interconnectContext context used for the send request
     * @throws DaemonError      with specific error code
     * @throws TimeoutException in case of communication timeout
     */
    public abstract void sendRequest(InterconnectContext interconnectContext) throws DaemonError, TimeoutException;


    /**
     * @param interconnectContext context used for the send request
     * @param responseClazz       class of the expected response
     * @param <R>                 response class
     * @return a response to the request
     * @throws DaemonError      with specific error code
     * @throws TimeoutException in case of communication timeout
     */
    public abstract <R> R syncRequest(InterconnectContext interconnectContext, Class<R> responseClazz) throws DaemonError, TimeoutException;

    @Override
    public final <D extends IDaemon> D create(final Class<D> daemon) {
        return DaemonMethodInvocationHandler.createProxy(daemon, this);
    }
}
