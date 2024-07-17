package de.taimos.dvalin.interconnect.core.spring.message;

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

import de.taimos.dvalin.interconnect.core.spring.DaemonMessageSenderHeader;
import de.taimos.dvalin.interconnect.core.spring.IDaemonMessageSender;
import de.taimos.dvalin.interconnect.model.InterconnectObject;

/**
 * Must be implemented for mocking interconnect communication via {@link IDaemonMessageSender}.
 */
public interface IMessageMock {

    /**
     * @param topic   Topic name
     * @param ico     InterconnectObject
     * @param headers Headers (optional)
     * @throws Exception If something went wrong
     */
    void sendToTopic(String topic, InterconnectObject ico, DaemonMessageSenderHeader... headers) throws Exception;

    /**
     * @param queue         Queue name
     * @param ico           InterconnectObject
     * @param correlationId Correlation id
     * @param headers       Headers (optional)
     * @throws Exception If something went wrong
     */
    void sendToQueue(String queue, InterconnectObject ico, String correlationId, DaemonMessageSenderHeader... headers) throws Exception;

}
