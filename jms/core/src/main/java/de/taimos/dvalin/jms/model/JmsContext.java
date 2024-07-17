package de.taimos.dvalin.jms.model;

import com.google.common.base.Preconditions;
import de.taimos.dvalin.jms.IJmsConnector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jms.Destination;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
public class JmsContext {
    public static class JmsContextBuilder extends AJmsContextBuilder<JmsContextBuilder> {

        /**
         * @param body of the send request
         * @return builder
         */
        @Nonnull
        public JmsContextBuilder withBody(Serializable body) {
            return super.withBody(body);
        }

    }

    @SuppressWarnings("unchecked")
    protected abstract static class AJmsContextBuilder<T extends AJmsContextBuilder<?>> {

        protected Destination destination;
        protected String destinationName;
        protected Serializable body;
        protected Map<String, Object> headers = new HashMap<>();
        protected boolean secure;
        protected Destination replyToDestination;
        protected String correlationId = UUID.randomUUID().toString();
        protected JmsTarget target;
        protected Long receiveTimeout = IJmsConnector.REQUEST_TIMEOUT;
        protected Long timeToLive = IJmsConnector.REQUEST_TIMEOUT;
        protected Integer priority = IJmsConnector.MSGPRIORITY;
        protected String selector;
        protected String replyToQueueName;

        protected AJmsContextBuilder() {
            //default constructor
        }

        public AJmsContextBuilder(JmsContext original) {
            this.destination = original.destination;
            this.destinationName = original.destinationName;
            this.body = original.body;
            this.headers = original.headers;
            this.secure = original.secure;
            this.replyToDestination = original.replyToDestination;
            this.correlationId = original.correlationId;
            this.target = original.target;
            this.receiveTimeout = original.receiveTimeout;
            this.timeToLive = original.timeToLive;
            this.priority = original.priority;
            this.selector = original.selector;
            this.replyToQueueName = original.replyToQueueName;
        }

        /**
         * @param destination for the send request
         * @return builder
         */
        @Nonnull
        public T withDestination(@Nullable Destination destination) {
            this.destination = destination;
            return (T) this;
        }

        /**
         * @param destinationName name of the destination
         * @return builder
         */
        @Nonnull
        public T withDestinationName(@Nullable String destinationName) {
            this.destinationName = destinationName;
            return (T) this;
        }

        /**
         * @param body of the send request
         * @return builder
         */
        @Nonnull
        protected T withBody(Serializable body) {
            this.body = body;
            return (T) this;
        }

        /**
         * @param headers of the send request
         * @return builder
         */
        @Nonnull
        public T withHeaders(@Nonnull Map<String, Object> headers) {
            this.headers.clear();
            this.headers.putAll(headers);
            return (T) this;
        }

        /**
         * @param secure the connection or not
         * @return builder
         */
        @Nonnull
        public T withSecure(boolean secure) {
            this.secure = secure;
            return (T) this;
        }

        /**
         * @param replyToDestination of the send request
         * @return builder
         */
        @Nonnull
        public T withReplyToDestination(@Nullable Destination replyToDestination) {
            this.replyToDestination = replyToDestination;
            return (T) this;
        }

        /**
         * @param replyToQueueName of the send request
         * @return builder
         */
        @Nonnull
        public T withReplyToQueueName(@Nullable String replyToQueueName) {
            this.replyToQueueName = replyToQueueName;
            return (T) this;
        }

        /**
         * @param correlationId of the send request
         * @return builder
         */
        @Nonnull
        public T withCorrelationId(@Nullable String correlationId) {
            this.correlationId = correlationId;
            return (T) this;
        }

        /**
         * @param target ({@link JmsTarget#QUEUE}, {@link JmsTarget#TOPIC} or {@link JmsTarget#DESTINATION}) of the send request.
         *               For {@link JmsTarget#QUEUE} or {@link JmsTarget#TOPIC} the {@link #withDestinationName(String)} needs to be supplied.
         *               For {@link JmsTarget#DESTINATION} {@link #withDestination(Destination)} is required.
         * @return builder
         */
        @Nonnull
        public T withTarget(@Nonnull JmsTarget target) {
            this.target = target;
            return (T) this;
        }

        /**
         * @param receiveTimeout of the send request
         * @return builder
         */
        @Nonnull
        public T withReceiveTimeout(Long receiveTimeout, @Nullable TimeUnit timeUnit) {
            if (receiveTimeout != null && timeUnit != null) {
                this.receiveTimeout = timeUnit.toMillis(receiveTimeout);
            } else if (receiveTimeout != null) {
                this.receiveTimeout = receiveTimeout;
            }
            return (T) this;
        }

        /**
         * @param timeToLive of the send request
         * @return builder
         */
        @Nonnull
        public T withTimeToLive(Long timeToLive, @Nullable TimeUnit timeUnit) {
            if (timeToLive != null && timeUnit != null) {
                this.timeToLive = timeUnit.toMillis(timeToLive);
            } else if (timeToLive != null) {
                this.timeToLive = timeToLive;
            }
            return (T) this;
        }

        /**
         * @param priority of the send request
         * @return builder
         */
        @Nonnull
        public T withPriority(@Nullable Integer priority) {
            this.priority = priority;
            return (T) this;
        }

        /**
         * @return an instance of the JmsContext filled with the context of the builder
         */
        @Nonnull
        public JmsContext build() {
            this.validate();
            if (this.selector == null) {
                this.selector = "JMSCorrelationID = '" + this.correlationId + "'";
            }

            return new JmsContext(this);
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
                case RECEPTION_CONTEXT:
                    break;
                default:
                    throw new UnsupportedOperationException("Target not supported: " + this.target);
            }
            Preconditions.checkNotNull(this.body, "Body was null");
        }
    }

    private final Destination destination;
    private final String destinationName;
    private final Serializable body;
    private final Map<String, Object> headers;
    private final boolean secure;
    private final Destination replyToDestination;
    private final String correlationId;
    private final JmsTarget target;
    private final Long receiveTimeout;
    private final Long timeToLive;
    private final Integer priority;

    private final String replyToQueueName;
    private final String selector;

    protected JmsContext(AJmsContextBuilder<?> builder) {
        this.destination = builder.destination;
        this.destinationName = builder.destinationName;
        this.body = builder.body;
        this.headers = builder.headers;
        this.secure = builder.secure;
        this.replyToDestination = builder.replyToDestination;
        this.correlationId = builder.correlationId;
        this.target = builder.target;
        this.receiveTimeout = builder.receiveTimeout;
        this.timeToLive = builder.timeToLive;
        this.priority = builder.priority;
        this.replyToQueueName = builder.replyToQueueName;
        this.selector = builder.selector;
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
    public Serializable getBody() {
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
        return this.replyToDestination != null ? this.replyToDestination.toString() : this.replyToQueueName;
    }

    /**
     * @return the replyToDestination
     */
    public Destination getReplyToDestination() {
        return this.replyToDestination;
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
    @Nonnull
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
        return this.receiveTimeout;
    }

    /**
     * @return the timeToLive
     */
    public long getTimeToLive() {
        return this.timeToLive;
    }

    /**
     * @return the priority
     */
    public int getPriority() {
        return this.priority;
    }

    /**
     * @return the selector
     */
    public String getSelector() {
        return this.selector;
    }
}
