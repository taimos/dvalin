package de.taimos.dvalin.interconnect.core;

/*-
 * #%L
 * Dvalin interconnect core library
 * %%
 * Copyright (C) 2016 - 2017 Taimos GmbH
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

import de.taimos.dvalin.interconnect.core.daemon.util.DaemonExceptionMapper;
import de.taimos.dvalin.interconnect.model.service.DaemonError;
import de.taimos.dvalin.jms.IJmsConnector;
import de.taimos.dvalin.jms.exceptions.InfrastructureException;
import de.taimos.dvalin.jms.exceptions.SerializationException;
import de.taimos.dvalin.jms.exceptions.TimeoutException;
import de.taimos.dvalin.jms.model.JmsContext.JmsContextBuilder;
import de.taimos.dvalin.jms.model.JmsTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * This class handles sending messages.
 *
 * @author fzwirn
 */
public abstract class AToTopicSender implements IToTopicSender {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());


    protected IJmsConnector jmsConnector;

    protected AToTopicSender(IJmsConnector jmsConnector) {
        this.jmsConnector = jmsConnector;
    }

    /**
     * @param object    the object
     * @param topicName name of the topic you want to use
     * @throws DaemonError is throw if there is a problem with sending the event
     */
    public void send(Serializable object, String topicName) throws DaemonError, TimeoutException {
        if (topicName == null) {
            this.logger.error("Invalid topic name: a non-null name is required");
            throw new IllegalArgumentException("Invalid topic name: a non-null name is required");
        }
        JmsContextBuilder context = new JmsContextBuilder().withTarget(JmsTarget.TOPIC)
            .withDestinationName(topicName).withBody(object);

        try {
            this.jmsConnector.send(context.build());
        } catch (InfrastructureException | SerializationException e) {
            DaemonExceptionMapper.mapAndThrow(e);
        }
    }
}
