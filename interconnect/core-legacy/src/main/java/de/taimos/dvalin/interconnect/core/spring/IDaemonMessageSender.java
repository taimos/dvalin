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

import javax.jms.Destination;

import de.taimos.dvalin.interconnect.core.daemon.DaemonResponse;
import de.taimos.dvalin.interconnect.model.InterconnectObject;


/**
 * Send a response.
 */
public interface IDaemonMessageSender {

    /**
     * Reply with a Daemon response.
     *
     * @param response Response
     * @throws Exception If something went wrong
     */
    void reply(DaemonResponse response) throws Exception;

    /**
     * Reply with a Daemon response.
     *
     * @param response Response
     * @param secure   (encrypted communication)
     * @throws Exception If something went wrong
     */
    void reply(DaemonResponse response, boolean secure) throws Exception;

    /**
     * Reply with an IVO.
     *
     * @param correlationID Correlation ID
     * @param replyTo       Reply to
     * @param ico           InterconnectObject
     * @throws Exception If something went wrong
     */
    void reply(String correlationID, Destination replyTo, InterconnectObject ico) throws Exception;

    /**
     * Reply with an IVO.
     *
     * @param correlationID Correlation ID
     * @param replyTo       Reply to
     * @param ico           InterconnectObject
     * @param secure        (encrypted communication)
     * @throws Exception If something went wrong
     */
    void reply(String correlationID, Destination replyTo, InterconnectObject ico, boolean secure) throws Exception;

    /**
     * @param topic   Topic name
     * @param ico     InterconnectObject
     * @param headers Headers (optional)
     * @throws Exception If something went wrong
     */
    void sendToTopic(String topic, InterconnectObject ico, DaemonMessageSenderHeader... headers) throws Exception;

    /**
     * @param queue   Queue name
     * @param ico     InterconnectObject
     * @param headers Headers (optional)
     * @throws Exception If something went wrong
     */
    void sendToQueue(String queue, InterconnectObject ico, DaemonMessageSenderHeader... headers) throws Exception;

    /**
     * @param topic   Topic name
     * @param ico     InterconnectObject
     * @param secure  (encrypted communication)
     * @param headers Headers (optional)
     * @throws Exception If something went wrong
     */
    void sendToTopic(String topic, InterconnectObject ico, boolean secure, DaemonMessageSenderHeader... headers) throws Exception;

    /**
     * @param queue   Queue name
     * @param ico     InterconnectObject
     * @param secure  (encrypted communication)
     * @param headers Headers (optional)
     * @throws Exception If something went wrong
     */
    void sendToQueue(String queue, InterconnectObject ico, boolean secure, DaemonMessageSenderHeader... headers) throws Exception;

    /**
     * @param queue         Queue name
     * @param ico           InterconnectObject
     * @param secure        (encrypted communication)
     * @param replyToQueue  Reply To queue (can be null)
     * @param correlationId Correlation id (can be null)
     * @param headers       Headers (optional)
     * @throws Exception If something went wrong
     */
    void sendToQueue(String queue, InterconnectObject ico, boolean secure, String replyToQueue, String correlationId, DaemonMessageSenderHeader... headers) throws Exception;

}
