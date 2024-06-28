package de.taimos.dvalin.jms.activemq;

import de.taimos.dvalin.jms.JmsConnector;
import de.taimos.dvalin.jms.crypto.ICryptoService;
import de.taimos.dvalin.jms.model.JmsTarget;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
public class ActiveMqJmsConnector extends JmsConnector {
    /**
     * @param connectionFactory to use with this connector
     * @param cryptoService     to use with this connector
     */
    public ActiveMqJmsConnector(ConnectionFactory connectionFactory, ICryptoService cryptoService) {
        super(connectionFactory, cryptoService);
    }

    @Override
    public Destination createDestination(JmsTarget type, String name) {
        switch (type) {
            case QUEUE:
                return new ActiveMQQueue(name);
            case TOPIC:
                return new ActiveMQTopic(name);
            case DESTINATION:
            default:
                throw new UnsupportedOperationException("Not supported for ActiveMQ Classic");
        }
    }
}
