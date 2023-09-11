package de.taimos.dvalin.interconnect.core.spring;

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

import java.util.HashMap;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import de.taimos.daemon.spring.annotations.ProdComponent;
import de.taimos.dvalin.interconnect.core.InterconnectConnector;
import de.taimos.dvalin.interconnect.core.MessageConnector;
import de.taimos.dvalin.interconnect.core.daemon.DaemonResponse;
import de.taimos.dvalin.interconnect.core.exceptions.InfrastructureException;
import de.taimos.dvalin.interconnect.model.CryptoException;
import de.taimos.dvalin.interconnect.model.InterconnectMapper;
import de.taimos.dvalin.interconnect.model.InterconnectObject;


/**
 * JMS Message sender.
 */
@ProdComponent
public final class DaemonMessageSender implements IDaemonMessageSender {

    /**
     * Logger.
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired()
    @Qualifier("jmsTemplate")
    private JmsTemplate template;


    /** */
    public DaemonMessageSender() {
        this.logger.trace("new bean instance created");
    }

    /**
     * @throws Exception If MessageConnector can not be started
     */
    @PostConstruct
    public void start() throws Exception {
        InterconnectConnector.start();
    }

    /** */
    @PreDestroy
    public void stop() {
        try {
            InterconnectConnector.stop();
        } catch (final InfrastructureException e) {
            this.logger.warn("Can not stop MessageConnector", e);
        }
    }

    private void sendIVO(final String correlationID, final Destination replyTo, final InterconnectObject ico, final boolean secure) throws Exception {
        final String json = InterconnectMapper.toJson(ico);
        this.logger.debug("TextMessage send: " + json);
        this.template.send(replyTo, new MessageCreator() {

            @Override
            public Message createMessage(final Session session) throws JMSException {
                final TextMessage textMessage = session.createTextMessage();
                textMessage.setStringProperty(InterconnectConnector.HEADER_ICO_CLASS, ico.getClass().getName());
                textMessage.setJMSCorrelationID(correlationID);
                textMessage.setText(json);
                if (secure) {
                    try {
                        MessageConnector.secureMessage(textMessage);
                    } catch (CryptoException e) {
                        throw new JMSException(e.getMessage());
                    }
                }
                return textMessage;
            }
        });
    }

    @Override
    public void reply(final DaemonResponse response) throws Exception {
        this.reply(response, false);
    }

    @Override
    public void reply(final String correlationID, final Destination replyTo, final InterconnectObject ico) throws Exception {
        this.reply(correlationID, replyTo, ico, false);
    }

    @Override
    public void reply(final DaemonResponse response, boolean secure) throws Exception {
        this.sendIVO(response.getRequest().getCorrelationID(), response.getRequest().getReplyTo(), response.getResponse(), secure);
    }

    @Override
    public void reply(final String correlationID, final Destination replyTo, final InterconnectObject ico, boolean secure) throws Exception {
        this.sendIVO(correlationID, replyTo, ico, secure);
    }

    @Override
    public void sendToTopic(final String topic, final InterconnectObject ico, final DaemonMessageSenderHeader... headers) throws Exception {
        this.sendToTopic(topic, ico, false, headers);
    }

    @Override
    public void sendToQueue(final String queue, final InterconnectObject ico, final DaemonMessageSenderHeader... headers) throws Exception {
        this.sendToQueue(queue, ico, false, headers);
    }

    private static HashMap<String, Object> wrapHeaders(final DaemonMessageSenderHeader... headers) {
        final HashMap<String, Object> h = new HashMap<>(headers.length);
        for (final DaemonMessageSenderHeader header : headers) {
            h.put(header.getField().getName(), header.getValue());
        }
        return h;
    }

    @Override
    public void sendToTopic(final String topic, final InterconnectObject ico, final boolean secure, final DaemonMessageSenderHeader... headers) throws Exception {
        InterconnectConnector.sendToTopic(topic, ico, DaemonMessageSender.wrapHeaders(headers), secure);
    }

    @Override
    public void sendToQueue(final String queue, final InterconnectObject ico, boolean secure, final DaemonMessageSenderHeader... headers) throws Exception {
        InterconnectConnector.sendToQueue(queue, ico, DaemonMessageSender.wrapHeaders(headers), secure);
    }

    @Override
    public void sendToQueue(final String queue, final InterconnectObject ico, final boolean secure, final String replyToQueue, final String correlationId, final DaemonMessageSenderHeader... headers) throws Exception {
        InterconnectConnector.sendToQueue(queue, ico, DaemonMessageSender.wrapHeaders(headers), secure, replyToQueue, correlationId);
    }

}
