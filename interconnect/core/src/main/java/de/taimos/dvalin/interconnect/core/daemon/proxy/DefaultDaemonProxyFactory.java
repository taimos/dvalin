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

import de.taimos.dvalin.interconnect.core.daemon.IDaemonMessageSender;
import de.taimos.dvalin.interconnect.core.daemon.model.InterconnectContext;
import de.taimos.dvalin.interconnect.core.daemon.util.DaemonExceptionMapper;
import de.taimos.dvalin.interconnect.model.service.DaemonError;
import de.taimos.dvalin.interconnect.core.exceptions.TimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Default daemon proxy factory
 *
 * @author psigloch, fzwirn
 */
@Component
public class DefaultDaemonProxyFactory extends ADaemonProxyFactory {


    private final IDaemonMessageSender messageSender;


    /**
     * @param aMessageSender Message sender
     */
    @Autowired
    public DefaultDaemonProxyFactory(final IDaemonMessageSender aMessageSender) {
        super();
        this.messageSender = aMessageSender;
    }

    @Override
    public void sendRequest(InterconnectContext interconnectContext) throws DaemonError, TimeoutException {
        try {
            this.messageSender.sendRequest(interconnectContext);
        } catch (Exception e) {
            DaemonExceptionMapper.mapAndThrow(e);
        }
    }

    @Override
    public <R> R syncRequest(InterconnectContext interconnectContext, Class<R> responseClazz) throws DaemonError, TimeoutException {
        return this.messageSender.syncRequest(interconnectContext, responseClazz);
    }
}
