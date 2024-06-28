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

import de.taimos.daemon.spring.annotations.ProdComponent;
import de.taimos.dvalin.interconnect.core.daemon.DaemonResponse;
import de.taimos.dvalin.interconnect.core.model.DvalinInterconnectJmsSendObject;
import de.taimos.dvalin.interconnect.model.InterconnectMapper;
import de.taimos.dvalin.interconnect.model.InterconnectObject;
import de.taimos.dvalin.jms.IJmsConnector;
import de.taimos.dvalin.jms.crypto.ICryptoService;
import de.taimos.dvalin.jms.exceptions.MessageCryptoException;
import de.taimos.dvalin.jms.model.DvalinJmsSendObject.DvalinJmsSendObjectBuilder;
import de.taimos.dvalin.jms.model.JmsTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.InvalidDestinationException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.HashMap;


/**
 * JMS Message sender.
 *
 * @author fzwirn
 */
@ProdComponent
public final class DaemonMessageSender implements IDaemonMessageSender {

    /**
     * Logger.
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("jmsTemplate")
    private JmsTemplate template;

    @Value("${interconnect.tempqueue.retry:100}")
    private int tempQueueRetry;

    @Autowired
    private IJmsConnector jmsConnector;
    @Autowired
    private ICryptoService cryptoService;

    /**
     *
     */
    public DaemonMessageSender() {
        this.logger.trace("new bean instance created");
    }

    private void sendIVO(final String correlationID, final Destination replyTo, final InterconnectObject ico, final boolean secure) throws Exception {
        final String json = InterconnectMapper.toJson(ico);
        this.logger.debug("TextMessage send: {}", json);
        try {
            this.template.send(replyTo, this.createMessageCreator(correlationID, ico, secure, json));
        } catch (InvalidDestinationException e) {
            if (e.getMessage() != null && e.getMessage().contains("temp-queue://")) {
                this.logger.warn("Retrying message send to {} after {}ms", replyTo, this.tempQueueRetry);
                Thread.sleep(this.tempQueueRetry);
                this.template.send(replyTo, this.createMessageCreator(correlationID, ico, secure, json));
            } else {
                throw e;
            }
        }
    }

    private MessageCreator createMessageCreator(String correlationID, InterconnectObject ico, boolean secure, String json) {
        return session -> {
            final TextMessage textMessage = session.createTextMessage();
            textMessage.setStringProperty(DvalinInterconnectJmsSendObject.HEADER_ICO_CLASS, ico.getClass().getName());
            textMessage.setJMSCorrelationID(correlationID);
            textMessage.setText(json);
            if (secure) {
                try {
                    return this.cryptoService.secureMessage(textMessage);
                } catch (MessageCryptoException e) {
                    throw new JMSException(e.getMessage());
                }
            }
            return textMessage;
        };
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
        this.sendIVO(response.getRequest().getCorrelationID(), response.getRequest().getReplyTo(),
            response.getResponse(), secure);
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
        DvalinJmsSendObjectBuilder builder = new DvalinJmsSendObjectBuilder();
        builder.withTarget(JmsTarget.TOPIC);
        builder.withDestinationName(topic);
        builder.withBody(InterconnectMapper.toJson(ico));
        builder.withHeaders(DaemonMessageSender.wrapHeaders(headers));
        builder.withSecure(secure);
        this.jmsConnector.send(builder.build());
    }

    @Override
    public void sendToQueue(final String queue, final InterconnectObject ico, boolean secure, final DaemonMessageSenderHeader... headers) throws Exception {
        DvalinJmsSendObjectBuilder builder = new DvalinJmsSendObjectBuilder();
        builder.withTarget(JmsTarget.QUEUE);
        builder.withDestinationName(queue);
        builder.withBody(InterconnectMapper.toJson(ico));
        builder.withHeaders(DaemonMessageSender.wrapHeaders(headers));
        builder.withSecure(secure);
        this.jmsConnector.send(builder.build());
    }

    @Override
    public void sendToQueue(final String queue, final InterconnectObject ico, final boolean secure, final String replyToQueue, final String correlationId, final DaemonMessageSenderHeader... headers) throws Exception {
        DvalinJmsSendObjectBuilder builder = new DvalinJmsSendObjectBuilder();
        builder.withTarget(JmsTarget.QUEUE);
        builder.withDestinationName(queue);
        builder.withBody(InterconnectMapper.toJson(ico));
        builder.withHeaders(DaemonMessageSender.wrapHeaders(headers));
        builder.withSecure(secure);
        builder.withReplyToQueueName(replyToQueue);
        builder.withCorrelationId(correlationId);
        this.jmsConnector.send(builder.build());
    }
}
