package de.taimos.dvalin.interconnect.core.model;

import com.google.common.base.Preconditions;
import de.taimos.dvalin.interconnect.model.InterconnectMapper;
import de.taimos.dvalin.interconnect.model.InterconnectObject;
import de.taimos.dvalin.jms.exceptions.InfrastructureException;
import de.taimos.dvalin.jms.model.DvalinJmsSendObject;
import de.taimos.dvalin.jms.model.JmsTarget;

import javax.jms.Destination;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
public class DvalinInterconnectJmsSendObject extends DvalinJmsSendObject {

    /**
     * name of the message header to specify interconnect request UUID
     */
    public static final String HEADER_REQUEST_UUID = "InterconnectRequestUUID";

    /**
     * name of the message header to specify interconnect ICO class
     */
    public static final String HEADER_ICO_CLASS = "InterconnectICOClass";

    protected DvalinInterconnectJmsSendObject(Object uuid, String queueName, InterconnectObject requestICO, Boolean secure, Long receiveTimeout, Long sendTimeout, Integer priority) throws InfrastructureException {
        super(null, queueName, DvalinInterconnectJmsSendObject.createBody(requestICO),
            DvalinInterconnectJmsSendObject.createHeaders(uuid, requestICO), secure, null, null, JmsTarget.QUEUE,
            receiveTimeout, sendTimeout, priority);
    }

    private static String createBody(InterconnectObject ico) throws InfrastructureException {
        try {
            return InterconnectMapper.toJson(ico);
        } catch (final IOException e) {
            throw new InfrastructureException("Failed to read message");
        }
    }

    private static Map<String, Object> createHeaders(Object uuid, InterconnectObject requestICO) {
        final Map<String, Object> headers = new HashMap<>(2);
        headers.put(DvalinInterconnectJmsSendObject.HEADER_REQUEST_UUID, uuid.toString());
        headers.put(DvalinInterconnectJmsSendObject.HEADER_ICO_CLASS, requestICO.getClass().getName());
        return headers;
    }

    public static class DvalinInterconnectJmsSendObjectBuilder extends DvalinJmsSendObjectBuilder {

        private Object uuid;
        private InterconnectObject requestICO;

        public DvalinInterconnectJmsSendObjectBuilder withUuid(Object uuid) {
            this.uuid = uuid;
            return this;
        }

        public DvalinInterconnectJmsSendObjectBuilder withRequestICO(InterconnectObject requestICO) {
            this.requestICO = requestICO;
            return this;
        }

        @Override
        public DvalinJmsSendObjectBuilder withDestination(Destination destination) {
            return super.withDestination(destination);
        }

        @Override
        public DvalinJmsSendObjectBuilder withDestinationName(String destinationName) {
            return super.withDestinationName(destinationName);
        }

        @Override
        public DvalinJmsSendObjectBuilder withBody(String body) {
            return super.withBody(body);
        }

        @Override
        public DvalinJmsSendObjectBuilder withHeaders(Map<String, Object> headers) {
            return super.withHeaders(headers);
        }

        @Override
        public DvalinJmsSendObjectBuilder withSecure(boolean secure) {
            return super.withSecure(secure);
        }

        @Override
        public DvalinJmsSendObjectBuilder withReplyToQueueName(String replyToQueueName) {
            return super.withReplyToQueueName(replyToQueueName);
        }

        @Override
        public DvalinJmsSendObjectBuilder withCorrelationId(String correlationId) {
            return super.withCorrelationId(correlationId);
        }

        @Override
        public DvalinJmsSendObjectBuilder withTarget(JmsTarget target) {
            return super.withTarget(target);
        }

        @Override
        public DvalinJmsSendObjectBuilder withReceiveTimeout(Long receiveTimeout) {
            return super.withReceiveTimeout(receiveTimeout);
        }

        @Override
        public DvalinJmsSendObjectBuilder withSendTimeout(Long sendTimeout) {
            return super.withSendTimeout(sendTimeout);
        }

        @Override
        public DvalinJmsSendObjectBuilder withPriority(Integer priority) {
            return super.withPriority(priority);
        }

        @Override
        protected void validate() {
            super.validate();
            Preconditions.checkNotNull(this.uuid, "Universally unique identifier of the request");
        }

        public DvalinInterconnectJmsSendObject build() throws InfrastructureException {
            if (this.body == null) {
                this.body = DvalinInterconnectJmsSendObject.createBody(this.requestICO);
            }
            this.validate();
            return new DvalinInterconnectJmsSendObject(this.uuid, super.destinationName, this.requestICO, this.secure,
                this.receiveTimeout, this.sendTimeout, this.priority);
        }
    }
}
