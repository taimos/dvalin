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

import de.taimos.dvalin.interconnect.core.daemon.model.InterconnectContext;
import de.taimos.dvalin.interconnect.model.service.DaemonError;
import de.taimos.dvalin.jms.exceptions.TimeoutException;

import java.util.concurrent.Future;


/**
 * Send a response.
 */
public interface IDaemonMessageSender {

    void sendRequest(InterconnectContext interconnectContext) throws DaemonError, TimeoutException;

    <R> R syncRequest(InterconnectContext interconnectContext, Class<R> responseClazz) throws DaemonError, TimeoutException;

    <R> Future<R> asyncRequest(InterconnectContext interconnectContext, Class<R> responseClazz);
}
