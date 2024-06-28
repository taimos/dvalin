package de.taimos.dvalin.jms;

import de.taimos.dvalin.jms.crypto.ICryptoService;
import de.taimos.dvalin.jms.exceptions.CommunicationFailureException;
import de.taimos.dvalin.jms.exceptions.CommunicationFailureException.CommunicationCause;
import de.taimos.dvalin.jms.exceptions.CreationException;
import de.taimos.dvalin.jms.exceptions.CreationException.Source;
import de.taimos.dvalin.jms.exceptions.InfrastructureException;
import de.taimos.dvalin.jms.exceptions.MessageCryptoException;
import de.taimos.dvalin.jms.exceptions.TimeoutException;
import de.taimos.dvalin.jms.model.DvalinJmsReceiveObject;
import de.taimos.dvalin.jms.model.DvalinJmsResponseObject;
import de.taimos.dvalin.jms.model.DvalinJmsSendObject;
import de.taimos.dvalin.jms.model.JmsTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
@Service
public abstract class JmsConnector implements IJmsConnector {

    private final ConnectionFactory connectionFactory;
    private final ICryptoService cryptoService;

    protected static final Logger logger = LoggerFactory.getLogger(JmsConnector.class);

    /**
     * @param connectionFactory to use with this connector
     * @param cryptoService     to use with this connector
     */
    @Autowired
    public JmsConnector(ConnectionFactory connectionFactory, ICryptoService cryptoService) {
        this.connectionFactory = connectionFactory;
        this.cryptoService = cryptoService;
    }

    @Override
    public void send(DvalinJmsSendObject object) throws MessageCryptoException, InfrastructureException {
        GetDestinationAction action = JmsConnector.getGetDestinationAction(object);
        this.sendToDestination(action, object.getBody(), object.getHeaders(), object.isSecure(),
            object.getSendTimeout(), object.getPriority(), object.getReplyToQueueName(), object.getCorrelationId());
    }

    @Override
    public DvalinJmsResponseObject request(DvalinJmsSendObject object) throws MessageCryptoException, InfrastructureException {
        TextMessage response = this.request(object.getDestinationName(), object.getBody(), object.getHeaders(),
            object.isSecure(), object.getReceiveTimeout(), object.getSendTimeout(), object.getPriority());
        return new DvalinJmsResponseObject(response);
    }

    @Override
    public DvalinJmsResponseObject receive(DvalinJmsReceiveObject object) throws InfrastructureException, MessageCryptoException {
        GetDestinationAction action = JmsConnector.getGetDestinationAction(object);
        final List<TextMessage> messages = this.receiveBulkFromDestination(action, object.getSelector(), 1,
            object.getReceiveTimeout(), object.isSecure());
        if (messages.size() != 1) {
            throw new CommunicationFailureException(CommunicationCause.RECEIVE);
        }
        return new DvalinJmsResponseObject(messages.get(0));
    }

    private static GetDestinationAction getGetDestinationAction(DvalinJmsSendObject object) {
        GetDestinationAction action = null;
        switch (object.getTarget()) {
            case DESTINATION:
                action = new GetSimpleDestinationAction(object.getDestination());
                break;
            case QUEUE:
                new GetResolveDestinationAction(true, object.getDestinationName());
                break;
            case TOPIC:
                new GetResolveDestinationAction(false, object.getDestinationName());
                break;
        }
        return action;
    }


    private void sendToDestination(final GetDestinationAction getDestinationAction, final String body, final Map<String, Object> headers, final boolean secure, long sendTimeout, int priority, final String replyToQueueName, final String correlationId) throws InfrastructureException, MessageCryptoException {
        Connection connection = null;
        try {
            connection = this.connectionFactory.createConnection();
            Session session = null;
            try {
                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                TextMessage txt = this.createTextMessage(session, body, headers, secure, replyToQueueName,
                    correlationId);
                final Destination destination;
                try {
                    destination = getDestinationAction.get(session);
                } catch (final JMSException e) {
                    throw new CreationException(Source.DESTINATION, e);
                }
                JmsConnector.createProducerAndSend(session, destination, txt, DeliveryMode.PERSISTENT, sendTimeout,
                    priority);
            } catch (final JMSException e) {
                throw new CreationException(Source.SESSION, e);
            } finally {
                try {
                    if (session != null) {
                        session.close();
                    }
                } catch (final JMSException e) {
                    JmsConnector.logger.warn("Can not close session", e);
                }
            }
        } catch (JMSException e) {
            throw new CreationException(Source.CONNECTION, e);
        } finally {
            try {

                if (connection != null) {
                    connection.close();
                }
            } catch (final JMSException e) {
                JmsConnector.logger.warn("Can not close connection", e);
            }
        }
    }


    private static void createProducerAndSend(Session session, Destination destination, TextMessage txt, int deliveryMode, long sendTimeout, int priority) throws InfrastructureException {
        MessageProducer producer = null;
        try {
            producer = session.createProducer(destination);
            try {
                producer.send(destination, txt, deliveryMode, priority, sendTimeout);
            } catch (JMSException e) {
                throw new CommunicationFailureException(CommunicationCause.SEND, e);
            }
        } catch (final JMSException e) {
            throw new CreationException(Source.PRODUCER, e);
        } finally {
            try {
                if (producer != null) {
                    producer.close();
                }
            } catch (final JMSException e) {
                JmsConnector.logger.warn("Can not close producer", e);
            }
        }
    }

    private List<TextMessage> receiveBulkFromDestination(final GetDestinationAction getDestinationAction, final String selector, final int maxSize, final long timeout, final boolean secure) throws InfrastructureException, MessageCryptoException {
        Connection connection = null;
        try {
            connection = this.connectionFactory.createConnection();
            return this.createSessionAndReceiveMessages(getDestinationAction, selector, maxSize, timeout, secure,
                connection);
        } catch (JMSException e) {
            throw new CreationException(Source.CONNECTION, e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (final JMSException e) {
                JmsConnector.logger.warn("Can not close connection", e);
            }
        }
    }

    private List<TextMessage> createSessionAndReceiveMessages(GetDestinationAction getDestinationAction, String selector, int maxSize, long timeout, boolean secure, Connection connection) throws CreationException, CommunicationFailureException, TimeoutException, MessageCryptoException {
        Session session = null;
        try {
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            final Destination responseQueue;
            try {
                responseQueue = getDestinationAction.get(session);
            } catch (final JMSException e) {
                throw new CreationException(Source.DESTINATION, e);
            }
            return this.createConsumerAndReceiveMessages(selector, maxSize, timeout, secure, session, responseQueue,
                connection);
        } catch (final JMSException e) {
            throw new CreationException(Source.SESSION, e);
        } finally {
            try {
                if (session != null) {
                    session.close();
                }
            } catch (final JMSException e) {
                JmsConnector.logger.warn("Can not close session", e);
            }
        }
    }

    private List<TextMessage> createConsumerAndReceiveMessages(String selector, int maxSize, long timeout, boolean secure, Session session, Destination responseQueue, Connection connection) throws CommunicationFailureException, TimeoutException, MessageCryptoException, CreationException {
        MessageConsumer consumer = null;
        try {
            consumer = session.createConsumer(responseQueue, selector);
            try {
                return this.receiveTextMessages(connection, consumer, maxSize, timeout, secure);
            } catch (final JMSException e) {
                throw new CommunicationFailureException(CommunicationCause.RECEIVE, e);
            } finally {
                connection.stop();
            }
        } catch (final JMSException e) {
            throw new CreationException(Source.CONSUMER, e);
        } finally {
            try {
                if (consumer != null) {
                    consumer.close();
                }
            } catch (final JMSException e) {
                JmsConnector.logger.warn("Can not close consumer", e);
            }
        }
    }

    private List<TextMessage> receiveTextMessages(Connection connection, MessageConsumer consumer, int maxSize, long timeout, boolean secure) throws JMSException, CommunicationFailureException, TimeoutException, MessageCryptoException {
        final List<TextMessage> messages = new ArrayList<>(maxSize);
        while (messages.size() < maxSize) {
            connection.start();
            final Message response;
            try {
                response = consumer.receive(timeout); // Wait for response.
            } catch (final JMSException e) {
                throw new CommunicationFailureException(CommunicationCause.RECEIVE, e);
            }
            if (response == null) {
                if (messages.isEmpty()) {
                    // first read timed out, so we throw a TimeoutException
                    throw new TimeoutException(timeout);
                }
                // consecutive read timed out, so we just return the result
                break;
            }
            if (response instanceof TextMessage) {
                final TextMessage txtRes = (TextMessage) response;
                if (secure) {
                    this.cryptoService.decryptMessage(txtRes);
                }
                messages.add(txtRes);
            } else {
                throw new CommunicationFailureException(CommunicationCause.INVALID_RESPONSE);
            }
        }
        return messages;
    }


    private TextMessage request(String queueName, String body, Map<String, Object> headers, boolean secure, long receiveTimeout, long sendTimeout, int priority) throws InfrastructureException, MessageCryptoException {
        final String correlationId = UUID.randomUUID().toString();

        Connection connection = null;
        try {
            connection = this.connectionFactory.createConnection();
            Session session = null;
            try {
                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                final TemporaryQueue temporaryQueue;
                final Queue requestQueue;
                try {
                    temporaryQueue = session.createTemporaryQueue();
                    requestQueue = session.createQueue(queueName);
                } catch (final JMSException e) {
                    throw new CreationException(Source.DESTINATION, e);
                }
                return this.createConsumerAndReceiveMessage(body, headers, secure, receiveTimeout, sendTimeout, priority, session,
                    temporaryQueue,
                    correlationId, requestQueue, connection);
            } catch (final JMSException e) {
                throw new CreationException(Source.SESSION, e);
            } finally {
                try {
                    if (session != null) {
                        session.close();
                    }
                } catch (final JMSException e) {
                    JmsConnector.logger.warn("Can not close session", e);
                }
            }
        } catch (JMSException e) {
            throw new CreationException(Source.CONNECTION, e);
        } finally {
            try {

                if (connection != null) {
                    connection.close();
                }
            } catch (final JMSException e) {
                JmsConnector.logger.warn("Can not close connection", e);
            }
        }

    }

    private TextMessage createConsumerAndReceiveMessage(String body, Map<String, Object> headers, boolean secure, long receiveTimeout, long sendTimeout, int priority, Session session, TemporaryQueue temporaryQueue, String correlationId, Queue requestQueue, Connection connection) throws MessageCryptoException, InfrastructureException {
        MessageConsumer consumer = null;
        try {
            consumer = session.createConsumer(temporaryQueue, "JMSCorrelationID = '" + correlationId + "'");
            final TextMessage txt = this.createTextMessage(session, body, headers, secure, temporaryQueue,
                correlationId);
            JmsConnector.createProducerAndSend(session, requestQueue, txt, DeliveryMode.NON_PERSISTENT,
                sendTimeout, priority);

            final Message receive;
            try {
                connection.start();
                receive = consumer.receive(receiveTimeout);
                connection.stop();
            } catch (final JMSException e) {
                throw new CommunicationFailureException(CommunicationCause.RECEIVE, e);
            }
            if (receive == null) {
                throw new TimeoutException(receiveTimeout);
            }
            if (receive instanceof TextMessage) {
                final TextMessage txtRes = (TextMessage) receive;
                if (secure) {
                    this.cryptoService.decryptMessage(txtRes);
                }
                return txtRes;
            }
            throw new CommunicationFailureException(CommunicationCause.INVALID_RESPONSE);
        } catch (final JMSException e) {
            throw new CreationException(Source.CONSUMER, e);
        } finally {
            try {
                if (consumer != null) {
                    consumer.close();
                }
            } catch (final JMSException e) {
                JmsConnector.logger.warn("Can not close consumer", e);
            }
            try {
                if (temporaryQueue != null) {
                    temporaryQueue.delete();
                }
            } catch (final JMSException e) {
                JmsConnector.logger.warn("Can not destroy temporary queue", e);
            }
        }
    }

    private TextMessage createTextMessage(Session session, String body, Map<String, Object> headers, boolean secure, String replyToQueueName, String correlationId) throws InfrastructureException, MessageCryptoException {
        Destination replyTo = null;
        if (replyToQueueName != null) {
            try {
                replyTo = session.createQueue(replyToQueueName);

            } catch (final JMSException e) {
                throw new CreationException(Source.REPLY_TO_DESTINATION, e);
            }
        }
        return this.createTextMessage(session, body, headers, secure, replyTo, correlationId);
    }

    private TextMessage createTextMessage(Session session, String body, Map<String, Object> headers, boolean secure, Destination replyToDestination, String correlationId) throws MessageCryptoException, InfrastructureException {
        final TextMessage txt;
        try {
            txt = session.createTextMessage(body);
            if (replyToDestination != null) {
                txt.setJMSReplyTo(replyToDestination);
            }
            if (correlationId != null) {
                txt.setJMSCorrelationID(correlationId);
            }
            if (headers != null) {
                final Set<Entry<String, Object>> entrySet = headers.entrySet();
                for (final Entry<String, Object> entry : entrySet) {
                    txt.setObjectProperty(entry.getKey(), entry.getValue());
                }
            }
            if (secure) {
                this.cryptoService.secureMessage(txt);
            }
        } catch (final JMSException e) {
            throw new CommunicationFailureException(CommunicationCause.FAILED_TO_CREATE_MESSAGE, e);
        }
        return txt;
    }

    @Override
    public abstract Destination createDestination(JmsTarget type, String name);


    private interface GetDestinationAction {
        Destination get(Session session) throws JMSException;
    }

    private static final class GetResolveDestinationAction implements GetDestinationAction {

        private final boolean isQueue;
        private final String destinationName;


        GetResolveDestinationAction(boolean isQueue, String destinationName) {
            super();
            this.isQueue = isQueue;
            this.destinationName = destinationName;
        }

        @Override
        public Destination get(final Session session) throws JMSException {
            if (this.isQueue) {
                return session.createQueue(this.destinationName);
            }
            return session.createTopic(this.destinationName);
        }
    }

    private static final class GetSimpleDestinationAction implements GetDestinationAction {

        private final Destination destination;


        GetSimpleDestinationAction(Destination destination) {
            super();
            this.destination = destination;
        }

        @Override
        public Destination get(final Session session) {
            return this.destination;
        }

    }
}
