package de.taimos.dvalin.interconnect.core.spring.test;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.taimos.dvalin.interconnect.core.spring.DaemonMessageSenderHeader;
import de.taimos.dvalin.interconnect.core.spring.message.IMessageMock;
import de.taimos.dvalin.interconnect.model.InterconnectMapper;
import de.taimos.dvalin.interconnect.model.InterconnectObject;

public class MessageSenderMock implements IMessageMock {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSenderMock.class);

    @Autowired
    private BrokerMock broker;

    @Override
    public void sendToTopic(String topic, InterconnectObject ico, DaemonMessageSenderHeader... headers) throws Exception {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("[{}]: {}", topic, InterconnectMapper.toJson(ico));
        }
        this.broker.send(topic, true, ico, headers);
    }

    @Override
    public void sendToQueue(String queue, InterconnectObject ico, String correlation, DaemonMessageSenderHeader... headers) throws Exception {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("[{}-{}]: {}", queue, correlation, InterconnectMapper.toJson(ico));
        }
        this.broker.send(queue, false, ico, headers);
    }

}
