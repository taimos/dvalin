package de.taimos.dvalin.jms.model;

import javax.jms.Destination;
import java.util.Map;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
public class DvalinJmsReceiveObject extends DvalinJmsSendObject {

    private final String selector;

    public DvalinJmsReceiveObject(Destination destination, String destinationName, String body, Map<String, Object> headers, boolean secure, String replyToQueueName, String correlationId, JmsTarget target, Long receiveTimeout, Long sendTimeout, Integer priority, String selector) {
        super(destination, destinationName, body, headers, secure, replyToQueueName, correlationId, target,
            receiveTimeout, sendTimeout, priority);
        this.selector = selector;
    }

    public static class DvalinJmsReceiveObjectBuilder extends DvalinJmsSendObjectBuilder {
        private String selector;
        private long timeout;

        public DvalinJmsReceiveObjectBuilder withSelector(String selector) {
            this.selector = selector;
            return this;
        }

        public DvalinJmsReceiveObject build() {
            super.validate();

            return new DvalinJmsReceiveObject(super.destination, super.destinationName, super.body, super.headers,
                super.secure, super.replyToQueueName, super.correlationId, super.target, super.receiveTimeout,
                super.sendTimeout, super.priority, this.selector);
        }

    }

    /**
     * @return the selector
     */
    public String getSelector() {
        return this.selector;
    }
}
