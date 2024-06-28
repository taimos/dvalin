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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.TextMessage;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.base.Preconditions;

import de.taimos.dvalin.interconnect.core.exceptions.InfrastructureException;
import de.taimos.dvalin.interconnect.model.InterconnectMapper;
import de.taimos.dvalin.interconnect.model.InterconnectObject;
import de.taimos.dvalin.jms.exceptions.MessageCryptoException;

/**
 * Connector to connect to Interconnect.
 */
public final class InterconnectConnector {

    /**
     * name of the message header to specify interconnect request UUID
     */
    public static final String HEADER_REQUEST_UUID = "InterconnectRequestUUID";

    /**
     * name of the message header to specify interconnect ICO class
     */
    public static final String HEADER_ICO_CLASS = "InterconnectICOClass";

    private static final String INTERCONNECT_OBJECT_NULL_ERROR = "Interconnect Object was null";
    private static final String QUEUE_NAME_NULL_ERROR = "Queue name was null";
    private static final String TOPIC_NAME_NULL_ERROR = "Topic name was null";

    /**
     * Singleton.
     */
    private InterconnectConnector() {
        super();
    }

    /**
     * @param brokerUrl the URL of the Interconnect message broker
     * @throws InfrastructureException of connection error
     */
    public static void start(final String brokerUrl) throws InfrastructureException {
        MessageConnector.start(brokerUrl);
    }

    /**
     * @throws InfrastructureException If MessageConnector can not be started
     */
    public static void start() throws InfrastructureException {
        MessageConnector.start();
    }

    /**
     * @throws InfrastructureException If MessageConnector can not be stopped
     */
    public static void stop() throws InfrastructureException {
        MessageConnector.stop();
    }

    /**
     * @param destination      Destination
     * @param ico              Interconnect Object
     * @param customHeaders    Headers (or null)
     * @param secure           Enable secure transport?
     * @param replyToQueueName Reply is send to this queue (or null)
     * @param correlationId    Correlation id (or null)
     * @throws InfrastructureException If an infrastructure error occurs
     * @throws MessageCryptoException         If the message could not be encrypted
     * @throws JsonGenerationException if the JSON data could not be generated
     * @throws JsonMappingException    if the object could not be mapped to a JSON string
     * @throws IOException             if an I/O related problem occurred
     */
    public static void sendToDestination(final Destination destination, final InterconnectObject ico, final Map<String, Object> customHeaders, final boolean secure, final String replyToQueueName, final String correlationId) throws InfrastructureException, MessageCryptoException, IOException {
        Preconditions.checkNotNull(destination, "Destination was null");
        Preconditions.checkNotNull(ico, INTERCONNECT_OBJECT_NULL_ERROR);
        final String body = InterconnectMapper.toJson(ico);
        final Map<String, Object> headers;
        if (customHeaders == null) {
            headers = new HashMap<>(1);
        } else {
            headers = new HashMap<>(customHeaders);
        }
        headers.put(InterconnectConnector.HEADER_ICO_CLASS, ico.getClass().getName());
        MessageConnector.sendToDestination(destination, body, headers, secure, replyToQueueName, correlationId);
    }

    /**
     * @param queueName        Queue name
     * @param ico              Interconnect Object
     * @param customHeaders    Headers (or null)
     * @param secure           Enable secure transport?
     * @param replyToQueueName Reply is send to this queue (or null)
     * @param correlationId    Correlation id (or null)
     * @throws InfrastructureException If an infrastructure error occurs
     * @throws MessageCryptoException         If the message could not be encrypted
     * @throws JsonGenerationException if the JSON data could not be generated
     * @throws JsonMappingException    if the object could not be mapped to a JSON string
     * @throws IOException             if an I/O related problem occurred
     */
    public static void sendToQueue(final String queueName, final InterconnectObject ico, final Map<String, Object> customHeaders, final boolean secure, final String replyToQueueName, final String correlationId) throws InfrastructureException, MessageCryptoException, IOException {
        Preconditions.checkNotNull(queueName, QUEUE_NAME_NULL_ERROR);
        Preconditions.checkNotNull(ico, INTERCONNECT_OBJECT_NULL_ERROR);
        final String body = InterconnectMapper.toJson(ico);
        final Map<String, Object> headers;
        if (customHeaders == null) {
            headers = new HashMap<>(1);
        } else {
            headers = new HashMap<>(customHeaders);
        }
        headers.put(InterconnectConnector.HEADER_ICO_CLASS, ico.getClass().getName());
        MessageConnector.sendToQueue(queueName, body, headers, secure, replyToQueueName, correlationId);
    }

    /**
     * @param queueName     Queue name
     * @param ico           Interconnect Object
     * @param customHeaders Headers
     * @param secure        Enable secure transport?
     * @throws InfrastructureException If an infrastructure error occurs
     * @throws MessageCryptoException         If the message could not be encrypted
     * @throws JsonGenerationException if the JSON data could not be generated
     * @throws JsonMappingException    if the object could not be mapped to a JSON string
     * @throws IOException             if an I/O related problem occurred
     */
    public static void sendToQueue(final String queueName, final InterconnectObject ico, final Map<String, Object> customHeaders, final boolean secure) throws InfrastructureException, MessageCryptoException, IOException {
        Preconditions.checkNotNull(queueName, QUEUE_NAME_NULL_ERROR);
        Preconditions.checkNotNull(ico, INTERCONNECT_OBJECT_NULL_ERROR);
        final String body = InterconnectMapper.toJson(ico);
        final Map<String, Object> headers;
        if (customHeaders == null) {
            headers = new HashMap<>(1);
        } else {
            headers = new HashMap<>(customHeaders);
        }
        headers.put(InterconnectConnector.HEADER_ICO_CLASS, ico.getClass().getName());
        MessageConnector.sendToQueue(queueName, body, headers, secure);
    }

    /**
     * @param queueName        Queue name
     * @param ico              Interconnect Object
     * @param customHeaders    Headers
     * @param replyToQueueName Reply is send to this queue (or null)
     * @param correlationId    Correlation id (or null)
     * @throws InfrastructureException If an infrastructure error occurs
     * @throws JsonGenerationException if the JSON data could not be generated
     * @throws JsonMappingException    if the object could not be mapped to a JSON string
     * @throws IOException             if an I/O related problem occurred
     */
    public static void sendToQueue(final String queueName, final InterconnectObject ico, final Map<String, Object> customHeaders, final String replyToQueueName, final String correlationId) throws InfrastructureException, IOException {
        Preconditions.checkNotNull(queueName, QUEUE_NAME_NULL_ERROR);
        Preconditions.checkNotNull(ico, INTERCONNECT_OBJECT_NULL_ERROR);
        final String body = InterconnectMapper.toJson(ico);
        final Map<String, Object> headers;
        if (customHeaders == null) {
            headers = new HashMap<>(1);
        } else {
            headers = new HashMap<>(customHeaders);
        }
        headers.put(InterconnectConnector.HEADER_ICO_CLASS, ico.getClass().getName());
        MessageConnector.sendToQueue(queueName, body, headers, replyToQueueName, correlationId);
    }

    /**
     * @param queueName     Queue name
     * @param ico           Interconnect Object
     * @param customHeaders Headers
     * @throws InfrastructureException If an infrastructure error occurs
     * @throws JsonGenerationException if the JSON data could not be generated
     * @throws JsonMappingException    if the object could not be mapped to a JSON string
     * @throws IOException             if an I/O related problem occurred
     */
    public static void sendToQueue(final String queueName, final InterconnectObject ico, final Map<String, Object> customHeaders) throws InfrastructureException, IOException {
        Preconditions.checkNotNull(queueName, QUEUE_NAME_NULL_ERROR);
        Preconditions.checkNotNull(ico, INTERCONNECT_OBJECT_NULL_ERROR);
        final String body = InterconnectMapper.toJson(ico);
        final Map<String, Object> headers;
        if (customHeaders == null) {
            headers = new HashMap<>(1);
        } else {
            headers = new HashMap<>(customHeaders);
        }
        headers.put(InterconnectConnector.HEADER_ICO_CLASS, ico.getClass().getName());
        MessageConnector.sendToQueue(queueName, body, headers);
    }

    /**
     * @param queueName Queue name
     * @param ico       Interconnect Object
     * @throws InfrastructureException If an infrastructure error occurs
     * @throws JsonGenerationException if the JSON data could not be generated
     * @throws JsonMappingException    if the object could not be mapped to a JSON string
     * @throws IOException             if an I/O related problem occurred
     */
    public static void sendToQueue(final String queueName, final InterconnectObject ico) throws InfrastructureException, IOException {
        Preconditions.checkNotNull(queueName, QUEUE_NAME_NULL_ERROR);
        Preconditions.checkNotNull(ico, INTERCONNECT_OBJECT_NULL_ERROR);
        InterconnectConnector.sendToQueue(queueName, ico, null);
    }

    /**
     * @param topicName     Topic name
     * @param ico           Interconnect Object
     * @param customHeaders Headers
     * @param secure        Enable secure transport?
     * @throws InfrastructureException If an infrastructure error occurs
     * @throws MessageCryptoException         If the message could not be encrypted
     * @throws JsonGenerationException if the JSON data could not be generated
     * @throws JsonMappingException    if the object could not be mapped to a JSON string
     * @throws IOException             if an I/O related problem occurred
     */
    public static void sendToTopic(final String topicName, final InterconnectObject ico, final Map<String, Object> customHeaders, final boolean secure) throws InfrastructureException, MessageCryptoException, IOException {
        Preconditions.checkNotNull(topicName, TOPIC_NAME_NULL_ERROR);
        Preconditions.checkNotNull(ico, INTERCONNECT_OBJECT_NULL_ERROR);
        final String body = InterconnectMapper.toJson(ico);
        final Map<String, Object> headers;
        if (customHeaders == null) {
            headers = new HashMap<>(1);
        } else {
            headers = new HashMap<>(customHeaders);
        }
        headers.put(InterconnectConnector.HEADER_ICO_CLASS, ico.getClass().getName());
        MessageConnector.sendToTopic(topicName, body, headers, secure);
    }

    /**
     * @param topicName     Topic name
     * @param ico           Interconnect Object
     * @param customHeaders Headers
     * @throws InfrastructureException If an infrastructure error occurs
     * @throws JsonGenerationException if the JSON data could not be generated
     * @throws JsonMappingException    if the object could not be mapped to a JSON string
     * @throws IOException             if an I/O related problem occurred
     */
    public static void sendToTopic(final String topicName, final InterconnectObject ico, final Map<String, Object> customHeaders) throws InfrastructureException, IOException {
        Preconditions.checkNotNull(topicName, TOPIC_NAME_NULL_ERROR);
        Preconditions.checkNotNull(ico, INTERCONNECT_OBJECT_NULL_ERROR);
        final String body = InterconnectMapper.toJson(ico);
        final Map<String, Object> headers;
        if (customHeaders == null) {
            headers = new HashMap<>(1);
        } else {
            headers = new HashMap<>(customHeaders);
        }
        headers.put(InterconnectConnector.HEADER_ICO_CLASS, ico.getClass().getName());
        MessageConnector.sendToTopic(topicName, body, headers);
    }

    /**
     * @param topicName Topic name
     * @param ico       Interconnect Object
     * @throws InfrastructureException If an infrastructure error occurs
     * @throws JsonGenerationException if the JSON data could not be generated
     * @throws JsonMappingException    if the object could not be mapped to a JSON string
     * @throws IOException             if an I/O related problem occurred
     */
    public static void sendToTopic(final String topicName, final InterconnectObject ico) throws InfrastructureException, IOException {
        Preconditions.checkNotNull(topicName, TOPIC_NAME_NULL_ERROR);
        Preconditions.checkNotNull(ico, INTERCONNECT_OBJECT_NULL_ERROR);
        InterconnectConnector.sendToTopic(topicName, ico, null);
    }

    /**
     * @param queueName Queue name
     * @param selector  JMS selector (or null or empty string := no selector)
     * @param timeout   Timeout in milliseconds to wait for an Interconnect Object
     * @param secure    Enable secure transport?
     * @return Interconnect Object
     * @throws InfrastructureException If an infrastructure error occurs
     * @throws MessageCryptoException         If the message could not be encrypted
     * @throws JsonGenerationException if the JSON data could not be generated
     * @throws JsonMappingException    if the object could not be mapped to a JSON string
     * @throws IOException             if an I/O related problem occurred
     */
    public static InterconnectObject receiveFromQueue(final String queueName, final String selector, final long timeout, final boolean secure) throws InfrastructureException, MessageCryptoException, IOException {
        Preconditions.checkNotNull(queueName, QUEUE_NAME_NULL_ERROR);
        final TextMessage response = MessageConnector.receiveFromQueue(queueName, selector, timeout, secure);
        try {
            return InterconnectMapper.fromJson(response.getText());
        } catch (final JMSException e) {
            throw new InfrastructureException("Failed to read message");
        }
    }

    /**
     * @param queueName Queue name
     * @param selector  JMS selector (or null or empty string := no selector)
     * @param timeout   Timeout in milliseconds to wait for an Interconnect Object
     * @param secure    Enable secure transport?
     * @return Response
     * @throws InfrastructureException If an infrastructure error occurs
     * @throws MessageCryptoException         If the message could not be encrypted
     * @throws JsonGenerationException if the JSON data could not be generated
     * @throws JsonMappingException    if the object could not be mapped to a JSON string
     * @throws IOException             if an I/O related problem occurred
     */
    public static Response receiveFromQueueEnhanced(final String queueName, final String selector, final long timeout, final boolean secure) throws InfrastructureException, MessageCryptoException, IOException {
        Preconditions.checkNotNull(queueName, QUEUE_NAME_NULL_ERROR);
        final TextMessage response = MessageConnector.receiveFromQueue(queueName, selector, timeout, secure);
        try {
            return new Response(InterconnectMapper.fromJson(response.getText()), response);
        } catch (final JMSException e) {
            throw new InfrastructureException("Failed to read message");
        }
    }

    /**
     * @param queueName Queue name
     * @param maxSize   Max messages to receive
     * @param selector  JMS selector (or null or empty string := no selector)
     * @param timeout   Timeout in milliseconds to wait for an Interconnect Object
     * @param secure    Enable secure transport?
     * @return Interconnect Object
     * @throws InfrastructureException If an infrastructure error occurs
     * @throws MessageCryptoException         If the message could not be encrypted
     * @throws JsonGenerationException if the JSON data could not be generated
     * @throws JsonMappingException    if the object could not be mapped to a JSON string
     * @throws IOException             if an I/O related problem occurred
     */
    public static List<InterconnectObject> receiveBulkFromQueue(final String queueName, final String selector, final int maxSize, final long timeout, final boolean secure) throws InfrastructureException, MessageCryptoException, IOException {
        Preconditions.checkNotNull(queueName, QUEUE_NAME_NULL_ERROR);
        final List<TextMessage> responses = MessageConnector.receiveBulkFromQueue(queueName, selector, maxSize, timeout, secure);
        final List<InterconnectObject> icos = new ArrayList<>(responses.size());
        try {
            for (final TextMessage response : responses) {
                icos.add(InterconnectMapper.fromJson(response.getText()));
            }
        } catch (final JMSException e) {
            throw new InfrastructureException("Failed to read message");
        }
        return icos;
    }

    /**
     * @param queueName Queue name
     * @param maxSize   Max messages to receive
     * @param selector  JMS selector (or null or empty string := no selector)
     * @param timeout   Timeout in milliseconds to wait for an Interconnect Object
     * @param secure    Enable secure transport?
     * @return Response
     * @throws InfrastructureException If an infrastructure error occurs
     * @throws MessageCryptoException         If the message could not be encrypted
     * @throws JsonGenerationException if the JSON data could not be generated
     * @throws JsonMappingException    if the object could not be mapped to a JSON string
     * @throws IOException             if an I/O related problem occurred
     */
    public static List<Response> receiveBulkFromQueueEnhanced(final String queueName, final String selector, final int maxSize, final long timeout, final boolean secure) throws InfrastructureException, MessageCryptoException, IOException {
        Preconditions.checkNotNull(queueName, QUEUE_NAME_NULL_ERROR);
        final List<TextMessage> responses = MessageConnector.receiveBulkFromQueue(queueName, selector, maxSize, timeout, secure);
        final List<Response> enhanceds = new ArrayList<>(responses.size());
        try {
            for (final TextMessage response : responses) {
                enhanceds.add(new Response(InterconnectMapper.fromJson(response.getText()), response));
            }
        } catch (final JMSException e) {
            throw new InfrastructureException("Failed to read message");
        }
        return enhanceds;
    }

    /**
     * @param topicName Topic name
     * @param selector  JMS selector (or null or empty string := no selector)
     * @param timeout   Timeout in milliseconds to wait for an Interconnect Object
     * @param secure    Enable secure transport?
     * @return Interconnect Object
     * @throws InfrastructureException If an infrastructure error occurs
     * @throws MessageCryptoException         If the message could not be encrypted
     * @throws JsonGenerationException if the JSON data could not be generated
     * @throws JsonMappingException    if the object could not be mapped to a JSON string
     * @throws IOException             if an I/O related problem occurred
     */
    public static InterconnectObject receiveFromTopic(final String topicName, final String selector, final long timeout, final boolean secure) throws InfrastructureException, MessageCryptoException, IOException {
        Preconditions.checkNotNull(topicName, TOPIC_NAME_NULL_ERROR);
        final TextMessage response = MessageConnector.receiveFromTopic(topicName, selector, timeout, secure);
        try {
            return InterconnectMapper.fromJson(response.getText());
        } catch (final JMSException e) {
            throw new InfrastructureException("Failed to read message");
        }
    }

    /**
     * @param topicName Topic name
     * @param selector  JMS selector (or null or empty string := no selector)
     * @param timeout   Timeout in milliseconds to wait for an Interconnect Object
     * @param secure    Enable secure transport?
     * @return Response
     * @throws InfrastructureException If an infrastructure error occurs
     * @throws MessageCryptoException         If the message could not be encrypted
     * @throws JsonGenerationException if the JSON data could not be generated
     * @throws JsonMappingException    if the object could not be mapped to a JSON string
     * @throws IOException             if an I/O related problem occurred
     */
    public static Response receiveFromTopicEnhanced(final String topicName, final String selector, final long timeout, final boolean secure) throws InfrastructureException, MessageCryptoException, IOException {
        Preconditions.checkNotNull(topicName, TOPIC_NAME_NULL_ERROR);
        final TextMessage response = MessageConnector.receiveFromTopic(topicName, selector, timeout, secure);
        try {
            return new Response(InterconnectMapper.fromJson(response.getText()), response);
        } catch (final JMSException e) {
            throw new InfrastructureException("Failed to read message");
        }
    }

    /**
     * @param topicName Topic name
     * @param selector  JMS selector (or null or empty string := no selector)
     * @param maxSize   Max messages to receive
     * @param timeout   Timeout in milliseconds to wait for an Interconnect Object
     * @param secure    Enable secure transport?
     * @return Interconnect Object
     * @throws InfrastructureException If an infrastructure error occurs
     * @throws MessageCryptoException         If the message could not be encrypted
     * @throws JsonGenerationException if the JSON data could not be generated
     * @throws JsonMappingException    if the object could not be mapped to a JSON string
     * @throws IOException             if an I/O related problem occurred
     */
    public static List<InterconnectObject> receiveBulkFromTopic(final String topicName, final String selector, final int maxSize, final long timeout, final boolean secure) throws InfrastructureException, MessageCryptoException, IOException {
        Preconditions.checkNotNull(topicName, TOPIC_NAME_NULL_ERROR);
        final List<TextMessage> responses = MessageConnector.receiveBulkFromTopic(topicName, selector, maxSize, timeout, secure);
        final List<InterconnectObject> icos = new ArrayList<>(responses.size());
        try {
            for (final TextMessage response : responses) {
                icos.add(InterconnectMapper.fromJson(response.getText()));
            }
        } catch (final JMSException e) {
            throw new InfrastructureException("Failed to read message");
        }
        return icos;
    }

    /**
     * @param topicName Topic name
     * @param selector  JMS selector (or null or empty string := no selector)
     * @param maxSize   Max messages to receive
     * @param timeout   Timeout in milliseconds to wait for an Interconnect Object
     * @param secure    Enable secure transport?
     * @return Response
     * @throws InfrastructureException If an infrastructure error occurs
     * @throws MessageCryptoException         If the message could not be encrypted
     * @throws JsonGenerationException if the JSON data could not be generated
     * @throws JsonMappingException    if the object could not be mapped to a JSON string
     * @throws IOException             if an I/O related problem occurred
     */
    public static List<Response> receiveBulkFromTopicEnhanced(final String topicName, final String selector, final int maxSize, final long timeout, final boolean secure) throws InfrastructureException, MessageCryptoException, IOException {
        Preconditions.checkNotNull(topicName, TOPIC_NAME_NULL_ERROR);
        final List<TextMessage> responses = MessageConnector.receiveBulkFromTopic(topicName, selector, maxSize, timeout, secure);
        final List<Response> enhanceds = new ArrayList<>(responses.size());
        try {
            for (final TextMessage response : responses) {
                enhanceds.add(new Response(InterconnectMapper.fromJson(response.getText()), response));
            }
        } catch (final JMSException e) {
            throw new InfrastructureException("Failed to read message");
        }
        return enhanceds;
    }


    /**
     * Response.
     */
    public static final class Response {

        private final InterconnectObject ico;

        private final TextMessage jmsTextMessage;


        /**
         * @param ico            Interconnect Object
         * @param jmsTextMessage JMS text message
         */
        public Response(final InterconnectObject ico, final TextMessage jmsTextMessage) {
            super();
            this.ico = ico;
            this.jmsTextMessage = jmsTextMessage;
        }

        /**
         * @return Interconnect Object
         */
        public InterconnectObject getICO() {
            return this.ico;
        }

        /**
         * @return JMS text message
         */
        public TextMessage getJMSTextMessage() {
            return this.jmsTextMessage;
        }

    }


    /**
     * @param uuid           Universally unique identifier of the request
     * @param queueName      Queue name
     * @param requestICO     Request Interconnect Object
     * @param customHeaders  Headers
     * @param secure         Enable secure transport?
     * @param receiveTimeout Request timeout (in milliseconds)
     * @param sendTimeout    Send timeout (in milliseconds)
     * @param priority       JMS priority
     * @return Response Interconnect Object
     * @throws InfrastructureException If an infrastructure error occurs
     * @throws MessageCryptoException         If the message could not be encrypted
     * @throws JsonGenerationException if the JSON data could not be generated
     * @throws JsonMappingException    if the object could not be mapped to a JSON string
     * @throws IOException             if an I/O related problem occurred
     */
    public static InterconnectObject request(final UUID uuid, final String queueName, final InterconnectObject requestICO, final Map<String, Object> customHeaders, final boolean secure, final long receiveTimeout, final long sendTimeout, final int priority) throws InfrastructureException, MessageCryptoException, IOException {
        Preconditions.checkNotNull(uuid, "Universally unique identifier of the request");
        Preconditions.checkNotNull(queueName, QUEUE_NAME_NULL_ERROR);
        Preconditions.checkNotNull(requestICO, "Request Interconnect Object");
        final String body = InterconnectMapper.toJson(requestICO);
        final Map<String, Object> headers;
        if (customHeaders == null) {
            headers = new HashMap<>(2);
        } else {
            headers = new HashMap<>(customHeaders);
        }
        headers.put(InterconnectConnector.HEADER_REQUEST_UUID, uuid.toString());
        headers.put(InterconnectConnector.HEADER_ICO_CLASS, requestICO.getClass().getName());
        final TextMessage response = MessageConnector.request(queueName, body, headers, secure, receiveTimeout, sendTimeout, priority);
        try {
            return InterconnectMapper.fromJson(response.getText());
        } catch (final JMSException e) {
            throw new InfrastructureException("Failed to read message");
        }
    }

    /**
     * @param uuid          Universally unique identifier of the request
     * @param queueName     Queue name
     * @param requestICO    Request Interconnect Object
     * @param customHeaders Headers
     * @return Response Interconnect Object
     * @throws InfrastructureException If an infrastructure error occurs
     * @throws JsonGenerationException if the JSON data could not be generated
     * @throws JsonMappingException    if the object could not be mapped to a JSON string
     * @throws IOException             if an I/O related problem occurred
     */
    public static InterconnectObject request(final UUID uuid, final String queueName, final InterconnectObject requestICO, final Map<String, Object> customHeaders) throws InfrastructureException, IOException {
        Preconditions.checkNotNull(uuid, "Universally unique identifier of the request");
        Preconditions.checkNotNull(queueName, QUEUE_NAME_NULL_ERROR);
        Preconditions.checkNotNull(requestICO, "Request Interconnect Object");
        final String body = InterconnectMapper.toJson(requestICO);
        final Map<String, Object> headers;
        if (customHeaders == null) {
            headers = new HashMap<>(2);
        } else {
            headers = new HashMap<>(customHeaders);
        }
        headers.put(InterconnectConnector.HEADER_REQUEST_UUID, uuid.toString());
        headers.put(InterconnectConnector.HEADER_ICO_CLASS, requestICO.getClass().getName());
        final TextMessage response = MessageConnector.request(queueName, body, headers);
        try {
            return InterconnectMapper.fromJson(response.getText());
        } catch (final JMSException e) {
            throw new InfrastructureException("Failed to read message");
        }
    }

    /**
     * @param uuid       Universally unique identifier of the request
     * @param queueName  Queue name
     * @param requestICO Request Interconnect Object
     * @return Response Interconnect Object
     * @throws InfrastructureException If an infrastructure error occurs
     * @throws JsonGenerationException if the JSON data could not be generated
     * @throws JsonMappingException    if the object could not be mapped to a JSON string
     * @throws IOException             if an I/O related problem occurred
     */
    public static InterconnectObject request(final UUID uuid, final String queueName, final InterconnectObject requestICO) throws InfrastructureException, IOException {
        Preconditions.checkNotNull(uuid, "Universally unique identifier of the request");
        Preconditions.checkNotNull(queueName, QUEUE_NAME_NULL_ERROR);
        Preconditions.checkNotNull(requestICO, "Request Interconnect Object");
        return InterconnectConnector.request(uuid, queueName, requestICO, null);
    }

}
