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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import java.io.IOException;
import java.io.Serializable;

/**
 * This class handles sending messages.
 *
 * @author fzwirn
 */
public abstract class AToTopicSender implements IToTopicSender {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected ConnectionFactory connectionFactory;

    protected AToTopicSender(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    protected Message getMessage(Serializable object, Session session) throws JMSException, IOException {
        return session.createObjectMessage(object);
    }

    /**
     * @param object    the object
     * @param topicName name of the topic you want to use
     */
    public void send(Serializable object, String topicName) {
        if (topicName == null) {
            this.logger.error("Invalid topic name: a non-null name is required");
            throw new IllegalArgumentException("Invalid topic name: a non-null name is required");
        }

        Connection connection = null;
        try {
            connection = this.connectionFactory.createConnection();
            this.createSessionAndSend(object, topicName, connection);
        } catch (JMSException | IOException e) {
            this.logger.error("Can not send message", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (final JMSException e) {
                    this.logger.warn("Can not close connection", e);
                }
            }
        }
    }

    private void createSessionAndSend(Serializable object, String topicName, Connection connection) throws JMSException, IOException {
        Session session = null;
        try {
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            final Topic topic = session.createTopic(topicName);
            this.createProducerAndSendToTopic(object, session, topic);
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (final JMSException e) {
                    this.logger.warn("Can not close session", e);
                }
            }
        }
    }

    private void createProducerAndSendToTopic(Serializable object, Session session, Topic topic) throws JMSException, IOException {
        MessageProducer topicmp = null;
        try {
            topicmp = session.createProducer(topic);
            topicmp.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            topicmp.send(this.getMessage(object, session));
        } finally {
            if (topicmp != null) {
                try {
                    topicmp.close();
                } catch (final JMSException e) {
                    this.logger.warn("Can not close producer", e);
                }
            }
        }
    }
}
