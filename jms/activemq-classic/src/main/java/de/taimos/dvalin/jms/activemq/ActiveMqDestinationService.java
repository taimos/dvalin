package de.taimos.dvalin.jms.activemq;

import de.taimos.dvalin.jms.IDestinationService;
import de.taimos.dvalin.jms.model.JmsTarget;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTempQueue;
import org.apache.activemq.command.ActiveMQTempTopic;
import org.apache.activemq.command.ActiveMQTopic;

import javax.annotation.Nonnull;
import javax.jms.Destination;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
public class ActiveMqDestinationService implements IDestinationService {

    /**
     * Default constructor
     */
    public ActiveMqDestinationService() {
    }

    @Override
    public Destination createDestination(@Nonnull JmsTarget type, @Nonnull String name) {
        switch (type) {
            case QUEUE:
                return new ActiveMQQueue(name);
            case TEMPORARY_QUEUE:
                return new ActiveMQTempQueue(name);
            case TOPIC:
                return new ActiveMQTopic(name);
            case TEMPORARY_TOPIC:
                return new ActiveMQTempTopic(name);
            case DESTINATION:
            case RECEPTION_CONTEXT:
            default:
                throw new UnsupportedOperationException("Not supported for ActiveMQ Classic");
        }
    }
}
