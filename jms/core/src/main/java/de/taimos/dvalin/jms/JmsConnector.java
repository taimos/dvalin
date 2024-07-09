package de.taimos.dvalin.jms;

import de.taimos.dvalin.jms.crypto.ICryptoService;
import de.taimos.dvalin.jms.exceptions.CommunicationFailureException;
import de.taimos.dvalin.jms.exceptions.CommunicationFailureException.CommunicationError;
import de.taimos.dvalin.jms.exceptions.CreationException;
import de.taimos.dvalin.jms.exceptions.CreationException.Source;
import de.taimos.dvalin.jms.exceptions.InfrastructureException;
import de.taimos.dvalin.jms.exceptions.SerializationException;
import de.taimos.dvalin.jms.exceptions.TimeoutException;
import de.taimos.dvalin.jms.model.JmsContext;
import de.taimos.dvalin.jms.model.JmsResponseContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
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
import java.util.Map.Entry;
import java.util.Set;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
public class JmsConnector implements IJmsConnector {

    private final ConnectionFactory connectionFactory;
    private final ICryptoService cryptoService;

    protected static final Logger logger = LoggerFactory.getLogger(JmsConnector.class);

    /**
     * @param connectionFactory to use with this connector
     * @param cryptoService     to use with this connector
     */
    public JmsConnector(ConnectionFactory connectionFactory, ICryptoService cryptoService) {
        this.connectionFactory = connectionFactory;
        this.cryptoService = cryptoService;
    }

    @Override
    public void send(@Nonnull JmsContext context) throws SerializationException, InfrastructureException {
        try (Connection connection = this.connectionFactory.createConnection()) {
            try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
                final Destination destination = JmsConnector.createDestination(session, context);
                Message txt = this.createTextMessageForDestination(session, context, null);
                JmsConnector.sendMessage(session, destination, txt,
                    context.getTimeToLive(), context.getPriority());
            } catch (final JMSException e) {
                throw new CreationException(Source.SESSION, e);
            }
        } catch (JMSException e) {
            throw new CreationException(Source.CONNECTION, e);
        }
    }

    @Override
    public JmsResponseContext<? extends Message> receive(@Nonnull JmsContext context) throws InfrastructureException, SerializationException {
        try (Connection connection = this.connectionFactory.createConnection()) {
            try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
                try (MessageConsumer consumer = session.createConsumer(JmsConnector.createDestination(session, context),
                    context.getSelector())) {
                    connection.start();
                    Message message = this.syncReceiveSingleMessage(consumer, context.getReceiveTimeout(),
                        context.isSecure());
                    return new JmsResponseContext<>(message);
                } catch (final JMSException e) {
                    throw new CreationException(Source.CONSUMER, e);
                }
            } catch (final JMSException e) {
                throw new CreationException(Source.SESSION, e);
            }
        } catch (JMSException e) {
            throw new CreationException(Source.CONNECTION, e);
        }
    }

    @Override
    public JmsResponseContext<? extends Message> request(@Nonnull JmsContext context) throws SerializationException, InfrastructureException {
        try (Connection connection = this.connectionFactory.createConnection()) {
            try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
                try {
                    final TemporaryQueue temporaryQueue = session.createTemporaryQueue();
                    final Queue requestQueue = session.createQueue(context.getDestinationName());
                    final Message txt = this.createTextMessageForDestination(session, context, temporaryQueue);

                    try (MessageConsumer consumer = session.createConsumer(temporaryQueue, context.getSelector())) {
                        connection.start();
                        JmsConnector.sendMessage(session, requestQueue, txt,
                            context.getTimeToLive(), context.getPriority());
                        Message response = this.syncReceiveSingleMessage(consumer, context.getReceiveTimeout(),
                            context.isSecure());
                        return new JmsResponseContext<>(response);
                    } catch (final JMSException e) {
                        throw new CreationException(Source.CONSUMER);
                    }
                } catch (final JMSException e) {
                    throw new CreationException(Source.DESTINATION, e);
                }
            } catch (final JMSException e) {
                throw new CreationException(Source.SESSION, e);
            }
        } catch (JMSException e) {
            throw new CreationException(Source.CONNECTION, e);
        }
    }

    private static void sendMessage(Session session, Destination destination, Message txt, long timeToLive, int priority) throws InfrastructureException {
        try (MessageProducer producer = session.createProducer(destination)) {
            try {
                producer.send(destination, txt, DeliveryMode.PERSISTENT, priority, timeToLive);
            } catch (JMSException e) {
                throw new CommunicationFailureException(CommunicationError.SEND, e);
            }
        } catch (final JMSException e) {
            throw new CreationException(Source.PRODUCER, e);
        }
    }

    @Override
    public List<Message> receiveBulkFromDestination(JmsContext context, final int maxMessages) throws InfrastructureException, SerializationException {
        try (Connection connection = this.connectionFactory.createConnection()) {
            try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
                Destination destination = JmsConnector.createDestination(session, context);
                List<Message> messages = new ArrayList<>();
                try (MessageConsumer consumer = session.createConsumer(destination, context.getSelector())) {
                    connection.start();
                    while (messages.size() < maxMessages) {
                        Message message = this.syncReceiveSingleMessage(consumer, context.getReceiveTimeout(),
                            context.isSecure());
                        messages.add(message);
                    }
                } catch (final JMSException e) {
                    throw new CreationException(Source.CONSUMER, e);
                }
                return messages;
            } catch (final JMSException e) {
                throw new CreationException(Source.SESSION, e);
            }
        } catch (JMSException e) {
            throw new CreationException(Source.CONNECTION, e);
        }
    }

    private static Destination createDestination(Session session, JmsContext context) throws CreationException {
        try {
            switch (context.getTarget()) {
                case DESTINATION:
                    return context.getDestination();
                case QUEUE:
                    return session.createQueue(context.getDestinationName());
                case TOPIC:
                    return session.createTopic(context.getDestinationName());
                default:
                    throw new CreationException(Source.DESTINATION);
            }
        } catch (JMSException e) {
            throw new CreationException(Source.DESTINATION);
        }
    }


    private Message syncReceiveSingleMessage(MessageConsumer consumer, long timeout, boolean secure) throws CommunicationFailureException, TimeoutException, SerializationException {
        try {
            final Message response = consumer.receive(timeout); // Wait for response.
            if (response == null) {
                throw new TimeoutException(timeout);
            }
            if (response instanceof TextMessage) {
                TextMessage txtRes = (TextMessage) response;
                if (secure) {
                    txtRes = (TextMessage) this.cryptoService.decryptMessage(txtRes);
                }
                return txtRes;
            }
            return response;
        } catch (final JMSException e) {
            throw new CommunicationFailureException(CommunicationError.RECEIVE, e);
        }
    }


    private Message createTextMessageForDestination(Session session, JmsContext sendContext, Destination replyToDestination) throws SerializationException, InfrastructureException {
        try {
            Message txt;
            if (sendContext.getBody() instanceof String) {
                txt = session.createTextMessage((String) sendContext.getBody());
            } else {
                txt = session.createObjectMessage(sendContext.getBody());
            }
            if (replyToDestination != null) {
                txt.setJMSReplyTo(replyToDestination);
            }
            if (sendContext.getCorrelationId() != null) {
                txt.setJMSCorrelationID(sendContext.getCorrelationId());
            }
            if (sendContext.getHeaders() != null) {
                final Set<Entry<String, Object>> entrySet = sendContext.getHeaders().entrySet();
                for (final Entry<String, Object> entry : entrySet) {
                    txt.setObjectProperty(entry.getKey(), entry.getValue());
                }
            }
            if (sendContext.isSecure()) {
                txt = this.cryptoService.secureMessage(txt);
            }
            return txt;
        } catch (final JMSException e) {
            throw new CreationException(Source.FAILED_TO_CREATE_MESSAGE, e);
        }
    }
}
