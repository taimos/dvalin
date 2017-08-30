package de.taimos.dvalin.interconnect.core;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import java.io.Serializable;

public abstract class ToTopicSender {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private volatile PooledConnectionFactory pooledConnectionFactory;

    protected ToTopicSender() {
        try {
            final String mqUrl = System.getProperty(MessageConnector.SYSPROP_IBROKERURL);
            final ActiveMQConnectionFactory mqFactory;
            if(mqUrl == null) {
                this.logger.warn("No " + MessageConnector.SYSPROP_IBROKERURL + " configured, using tcp://localhost:61616.");
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
        } catch(Exception e) {
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
     * @param object    the object
     * @param topicName name of the topic you want to use
     */
    public void send(Serializable object, String topicName) {
        Connection connection = null;
        try {
            connection = this.pooledConnectionFactory.createConnection();
            Session session = null;
            try {
                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                final Topic topic = session.createTopic(topicName);
                MessageProducer topicmp = null;
                try {
                    topicmp = session.createProducer(topic);
                    topicmp.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
                    topicmp.send(session.createObjectMessage(object));
                } finally {
                    if(topicmp != null) {
                        try {
                            topicmp.close();
                        } catch(final JMSException e) {
                            this.logger.warn("Can not close producer", e);
                        }
                    }
                }
            } finally {
                if(session != null) {
                    try {
                        session.close();
                    } catch(final JMSException e) {
                        this.logger.warn("Can not close session", e);
                    }
                }
            }
        } catch(JMSException e) {
            this.logger.error("Can not send message", e);
        } finally {
            if(connection != null) {
                try {
                    connection.close();
                } catch(final JMSException e) {
                    this.logger.warn("Can not close connection", e);
                }
            }
        }
    }
}
