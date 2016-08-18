/**
 *
 */
package de.taimos.dvalin.interconnect.core;

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

import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IVORefreshSender {

    private static IVORefreshSender instance;

    private volatile PooledConnectionFactory pooledConnectionFactory;

    /**
     * the tcc update topic
     */
    public static final String TCC_UPDATE_TOPIC = "TCCUpdateTopic";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * @return the singleton
     */
    public static IVORefreshSender getInstance() {
        if (IVORefreshSender.instance == null) {
            IVORefreshSender.instance = new IVORefreshSender();
        }
        return IVORefreshSender.instance;
    }

    private IVORefreshSender() {
        try {
            final String mqUrl = System.getProperty(MessageConnector.SYSPROP_IBROKERURL);
            final ActiveMQConnectionFactory mqFactory;
            if (mqUrl == null) {
                this.logger.warn("No " + MessageConnector.SYSPROP_IBROKERURL + " configuredd, using tcp://localhost:61616.");
                mqFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            } else {
                mqFactory = new ActiveMQConnectionFactory(mqUrl);
            }
            this.pooledConnectionFactory = new PooledConnectionFactory(mqFactory);
            this.pooledConnectionFactory.setCreateConnectionOnStartup(true);
            // this.pooledConnectionFactory.setExpiryTimeout(60000);
            this.pooledConnectionFactory.setIdleTimeout(0);
            this.pooledConnectionFactory.setMaxConnections(3);
            this.pooledConnectionFactory.setMaximumActiveSessionPerConnection(100);
            this.pooledConnectionFactory.setTimeBetweenExpirationCheckMillis(30000);
            this.pooledConnectionFactory.setBlockIfSessionPoolIsFull(false);

            // start connection pool
            this.pooledConnectionFactory.start();
        } catch (Exception e) {
            this.logger.error("Failed to setup the message queues", e);
        }
    }

    /**
     * teardown for the message queues
     */
    public void mqTeardown() {
        this.pooledConnectionFactory.stop();
    }

    /**
     * @param object the object
     */
    public void send(Serializable object) {
        Connection connection = null;
        try {
            connection = this.pooledConnectionFactory.createConnection();
            Session updateSession = null;
            try {
                updateSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                final Topic updateTopic = updateSession.createTopic(IVORefreshSender.TCC_UPDATE_TOPIC);
                MessageProducer utopicmp = null;
                try {
                    utopicmp = updateSession.createProducer(updateTopic);
                    utopicmp.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

                    utopicmp.send(updateSession.createObjectMessage(object));

                } finally {
                    if (utopicmp != null) {
                        try {
                            utopicmp.close();
                        } catch (final JMSException e) {
                            this.logger.warn("Can not close producer", e);
                        }
                    }
                }
            } finally {
                if (updateSession != null) {
                    try {
                        updateSession.close();
                    } catch (final JMSException e) {
                        this.logger.warn("Can not close session", e);
                    }
                }
            }
        } catch (JMSException e) {
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
}
