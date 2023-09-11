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

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.taimos.dvalin.interconnect.core.daemon.ADaemonProxyFactory;
import de.taimos.dvalin.interconnect.core.daemon.IDaemonProxyFactory;
import de.taimos.dvalin.interconnect.core.daemon.IDaemonRequestResponse;
import de.taimos.dvalin.interconnect.model.InterconnectObject;
import de.taimos.dvalin.interconnect.model.service.IDaemon;

@Component
public class DaemonProxyFactory implements IDaemonProxyFactory {

    @Autowired
    private IDaemonRequestResponse requestResponse;

    @Autowired
    private IDaemonMessageSender messageSender;

    private DaemonProxyFactoryImpl impl;


    private static final class DaemonProxyFactoryImpl extends ADaemonProxyFactory {

        private final IDaemonRequestResponse requestResponse;

        private final IDaemonMessageSender messageSender;


        /**
         * @param aRequestResponse Request & response
         * @param aMessageSender   Message sender
         */
        public DaemonProxyFactoryImpl(final IDaemonRequestResponse aRequestResponse, final IDaemonMessageSender aMessageSender) {
            super();
            this.requestResponse = aRequestResponse;
            this.messageSender = aMessageSender;
        }

        @Override
        protected void sendToQueue(final UUID uuid, final String queue, final InterconnectObject ico, final boolean secure) throws Exception {
            this.messageSender.sendToQueue(queue, ico, secure, DaemonMessageSenderHeader.createRequestUUID(uuid));
        }

        @Override
        protected <R> R syncRequest(final UUID uuid, final String queue, final InterconnectObject request, final Class<R> responseClazz, final long timeout, final TimeUnit unit, final boolean secure) throws ExecutionException {
            return this.requestResponse.sync(uuid, queue, request, responseClazz, timeout, unit, secure);
        }

    }


    /** */
    @PostConstruct
    public void start() {
        this.impl = new DaemonProxyFactoryImpl(this.requestResponse, this.messageSender);
    }

    @Override
    public <D extends IDaemon> D create(Class<D> daemon) {
        return this.impl.create(daemon);
    }

}
