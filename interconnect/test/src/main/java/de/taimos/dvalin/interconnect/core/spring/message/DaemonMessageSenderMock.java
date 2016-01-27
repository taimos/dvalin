package de.taimos.dvalin.interconnect.core.spring.message;

import javax.jms.Destination;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.taimos.daemon.spring.annotations.TestComponent;
import de.taimos.dvalin.interconnect.core.daemon.DaemonResponse;
import de.taimos.dvalin.interconnect.core.spring.DaemonMessageSenderHeader;
import de.taimos.dvalin.interconnect.core.spring.IDaemonMessageSender;
import de.taimos.dvalin.interconnect.model.InterconnectObject;

/**
 * Mock implementation of IDaemonMessageSender
 */
@TestComponent
public final class DaemonMessageSenderMock implements IDaemonMessageSender {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired(required = false)
    private IMessageMock messageMock;


    /** */
    public DaemonMessageSenderMock() {
        this.logger.trace("new bean instance created");
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
        throw new UnsupportedOperationException();
    }

    @Override
    public void reply(final String correlationID, final Destination replyTo, final InterconnectObject ico, boolean secure) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendToTopic(final String topic, final InterconnectObject ico, final DaemonMessageSenderHeader... headers) throws Exception {
        this.sendToTopic(topic, ico, false, headers);
    }

    @Override
    public void sendToQueue(final String queue, final InterconnectObject ico, final DaemonMessageSenderHeader... headers) throws Exception {
        this.sendToQueue(queue, ico, false, headers);
    }

    @Override
    public void sendToTopic(final String topic, final InterconnectObject ico, boolean secure, final DaemonMessageSenderHeader... headers) throws Exception {
        if (this.messageMock == null) {
            throw new UnsupportedOperationException("No messageMock");
        }
        this.messageMock.sendToTopic(topic, ico, headers);
    }

    @Override
    public void sendToQueue(final String queue, final InterconnectObject ico, boolean secure, final DaemonMessageSenderHeader... headers) throws Exception {
        if (this.messageMock == null) {
            throw new UnsupportedOperationException("No messageMock");
        }
        this.messageMock.sendToQueue(queue, ico, null, headers);
    }

    @Override
    public void sendToQueue(final String queue, final InterconnectObject ico, final boolean secure, final String replyToQueue, final String correlationId, final DaemonMessageSenderHeader... headers) throws Exception {
        if (this.messageMock == null) {
            throw new UnsupportedOperationException("No messageMock");
        }
        this.messageMock.sendToQueue(queue, ico, correlationId, headers);
    }

}
