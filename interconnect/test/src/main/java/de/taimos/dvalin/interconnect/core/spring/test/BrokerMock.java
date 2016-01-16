package de.taimos.dvalin.interconnect.core.spring.test;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.jms.MessageListener;

import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.activemq.command.ActiveMQTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.taimos.dvalin.interconnect.core.InterconnectConnector;
import de.taimos.dvalin.interconnect.core.spring.DaemonMessageSenderHeader;
import de.taimos.dvalin.interconnect.model.InterconnectMapper;
import de.taimos.dvalin.interconnect.model.InterconnectObject;


public class BrokerMock {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerMock.class);

    private final ConcurrentHashMap<String, MessageListener> listeners = new ConcurrentHashMap<>();
    private final Executor exec = Executors.newCachedThreadPool();

    @Autowired
    private InterconnectRequestMock requestMock;


    /**
     * @param listeners the listeners map
     */
    public BrokerMock(Map<String, MessageListener> listeners) {
        this.listeners.putAll(listeners);
    }

    /**
     * @param destination the destination name
     * @param topic       <code>true</code> for topic; <code>false</code> for queue
     * @param ico         the object to send
     * @param headers     optional headers
     */
    public void send(final String destination, final boolean topic, final InterconnectObject ico, final DaemonMessageSenderHeader... headers) {
        // Check registered RequestMocks
        if (this.requestMock.hasHandler(destination)) {
            UUID uuid = null;
            for (DaemonMessageSenderHeader head : headers) {
                if (head.getField() == DaemonMessageSenderHeader.Field.RequestUUID) {
                    uuid = UUID.fromString((String) head.getValue());
                }
            }
            this.requestMock.receive(uuid == null ? UUID.randomUUID() : uuid, destination, ico);
            return;
        }

        final String destName = (topic ? "topic://" : "queue://") + destination;
        if (!BrokerMock.this.listeners.containsKey(destName)) {
            BrokerMock.LOGGER.error("Missing destination {}", destName);
            throw new RuntimeException();
        }
        this.exec.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    final ActiveMQDestination dest;
                    if (topic) {
                        dest = new ActiveMQTopic(destination);
                    } else {
                        dest = new ActiveMQQueue(destination);
                    }

                    ActiveMQTextMessage msg = new ActiveMQTextMessage();
                    msg.setDestination(dest);
                    msg.setText(InterconnectMapper.toJson(ico));
                    msg.setObjectProperty(InterconnectConnector.HEADER_ICO_CLASS, ico.getClass().getName());
                    if (headers != null) {
                        for (DaemonMessageSenderHeader header : headers) {
                            msg.setObjectProperty(header.getField().getName(), header.getValue());
                        }
                    }
                    BrokerMock.this.listeners.get(destName).onMessage(msg);
                } catch (Exception e) {
                    BrokerMock.LOGGER.error("Failed to send message", e);
                }

            }
        });
    }
}
