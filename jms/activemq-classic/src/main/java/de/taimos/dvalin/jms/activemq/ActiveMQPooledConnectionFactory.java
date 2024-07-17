package de.taimos.dvalin.jms.activemq;

import org.apache.activemq.pool.PooledConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.ConnectionFactory;

/**
 * Copyright 2022 Cinovo AG<br>
 * <br>
 *
 * @author aeichel
 */
public class ActiveMQPooledConnectionFactory extends PooledConnectionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveMQPooledConnectionFactory.class);

    /**
     * @param connectionFactory a connection factory
     * @return initialized connection pool
     */
    public ActiveMQPooledConnectionFactory initDefault(ConnectionFactory connectionFactory) {
        try {

            this.setConnectionFactory(connectionFactory);
            this.setCreateConnectionOnStartup(true);
            this.setIdleTimeout(0);
            this.setMaxConnections(3);
            this.setMaximumActiveSessionPerConnection(100);
            this.setTimeBetweenExpirationCheckMillis(30000);
            this.setBlockIfSessionPoolIsFull(false);

            // start connection pool
            this.start();
        } catch (Exception e) {
            ActiveMQPooledConnectionFactory.LOGGER.error("Failed to setup the message queues", e);
        }
        return this;
    }
}
