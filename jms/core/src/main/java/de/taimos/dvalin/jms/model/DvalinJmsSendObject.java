package de.taimos.dvalin.jms.model;

import com.google.common.base.Preconditions;
import de.taimos.dvalin.jms.IJmsConnector;
import de.taimos.dvalin.jms.exceptions.InfrastructureException;

import javax.jms.Destination;
import java.util.Map;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
public class DvalinJmsSendObject {
    private final Destination destination;
    private final String destinationName;
    private final String body;
    private final Map<String, Object> headers;
    private final boolean secure;
    private final String replyToQueueName;
    private final String correlationId;
    private final JmsTarget target;
    private final Long receiveTimeout;
    private final Long sendTimeout;
    private final Integer priority;

    protected DvalinJmsSendObject(Destination destination, String destinationName, String body, Map<String, Object> headers, boolean secure, String replyToQueueName, String correlationId, JmsTarget target, Long receiveTimeout, Long sendTimeout, Integer priority) {
        this.destination = destination;
        this.destinationName = destinationName;
        this.body = body;
        this.headers = headers;
        this.secure = secure;
        this.replyToQueueName = replyToQueueName;
        this.correlationId = correlationId;
        this.target = target;
        this.receiveTimeout = receiveTimeout;
        this.sendTimeout = sendTimeout;
        this.priority = priority;
    }

    public static class DvalinJmsSendObjectBuilder {

        protected Destination destination;
        protected String destinationName;
        protected String body;
        protected Map<String, Object> headers;
        protected boolean secure;
        protected String replyToQueueName;
        protected String correlationId;
        protected JmsTarget target;
        protected Long receiveTimeout;
        protected Long sendTimeout;
        protected Integer priority;

        public DvalinJmsSendObjectBuilder withDestination(Destination destination) {
            this.destination = destination;
            return this;
        }

        public DvalinJmsSendObjectBuilder withDestinationName(String destinationName) {
            this.destinationName = destinationName;
            return this;
        }

        public DvalinJmsSendObjectBuilder withBody(String body) {
            this.body = body;
            return this;
        }

        public DvalinJmsSendObjectBuilder withHeaders(Map<String, Object> headers) {
            this.headers = headers;
            return this;
        }

        public DvalinJmsSendObjectBuilder withSecure(boolean secure) {
            this.secure = secure;
            return this;
        }

        public DvalinJmsSendObjectBuilder withReplyToQueueName(String replyToQueueName) {
            this.replyToQueueName = replyToQueueName;
            return this;
        }

        public DvalinJmsSendObjectBuilder withCorrelationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public DvalinJmsSendObjectBuilder withTarget(JmsTarget target) {
            this.target = target;
            return this;
        }

        public DvalinJmsSendObjectBuilder withReceiveTimeout(Long receiveTimeout) {
            this.receiveTimeout = receiveTimeout;
            return this;
        }

        public DvalinJmsSendObjectBuilder withSendTimeout(Long sendTimeout) {
            this.sendTimeout = sendTimeout;
            return this;
        }

        public DvalinJmsSendObjectBuilder withPriority(Integer priority) {
            this.priority = priority;
            return this;
        }

        public DvalinJmsSendObject build() throws InfrastructureException {
            this.validate();

            return new DvalinJmsSendObject(this.destination, this.destinationName, this.body, this.headers, this.secure,
                this.replyToQueueName, this.correlationId, this.target, this.receiveTimeout, this.sendTimeout,
                this.priority);
        }

        protected void validate() {
            Preconditions.checkNotNull(this.target, "JMS Target not set");
            switch (this.target) {
                case DESTINATION:
                    Preconditions.checkNotNull(this.destination, "Destination was null");
                    break;
                case QUEUE:
                    Preconditions.checkNotNull(this.destinationName, "Queue name was null");
                    break;
                case TOPIC:
                    Preconditions.checkNotNull(this.destinationName, "Topic name was null");
                    break;
            }
            Preconditions.checkNotNull(this.body, "Body was null");
        }
    }

    /**
     * @return the destination
     */
    public Destination getDestination() {
        return this.destination;
    }

    /**
     * @return the body
     */
    public String getBody() {
        return this.body;
    }

    /**
     * @return the headers
     */
    public Map<String, Object> getHeaders() {
        return this.headers;
    }

    /**
     * @return the secure
     */
    public boolean isSecure() {
        return this.secure;
    }

    /**
     * @return the replyToQueueName
     */
    public String getReplyToQueueName() {
        return this.replyToQueueName;
    }

    /**
     * @return the correlationId
     */
    public String getCorrelationId() {
        return this.correlationId;
    }

    /**
     * @return the target
     */
    public JmsTarget getTarget() {
        return this.target;
    }

    /**
     * @return the destinationName
     */
    public String getDestinationName() {
        return this.destinationName;
    }

    /**
     * @return the receiveTimeout
     */
    public long getReceiveTimeout() {
        return this.receiveTimeout != null ? this.receiveTimeout : IJmsConnector.REQUEST_TIMEOUT;
    }

    /**
     * @return the sendTimeout
     */
    public long getSendTimeout() {
        return this.sendTimeout != null ? this.sendTimeout : IJmsConnector.REQUEST_TIMEOUT;
    }

    /**
     * @return the priority
     */
    public int getPriority() {
        return this.priority != null ? this.priority : IJmsConnector.MSGPRIORITY;
    }
}
