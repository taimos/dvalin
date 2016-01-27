package de.taimos.dvalin.interconnect.core.spring.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.taimos.dvalin.interconnect.core.spring.DaemonMessageSenderHeader;
import de.taimos.dvalin.interconnect.core.spring.message.IMessageMock;
import de.taimos.dvalin.interconnect.model.InterconnectMapper;
import de.taimos.dvalin.interconnect.model.InterconnectObject;

public class MessageSenderMock implements IMessageMock {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSenderMock.class);

    @Autowired
    private BrokerMock broker;


    @Override
    public void sendToTopic(String topic, InterconnectObject ico, DaemonMessageSenderHeader... headers) throws Exception {
        MessageSenderMock.LOGGER.info("[{}]: {}", topic, InterconnectMapper.toJson(ico));
        this.broker.send(topic, true, ico, headers);
    }

    @Override
    public void sendToQueue(String queue, InterconnectObject ico, String correlation, DaemonMessageSenderHeader... headers) throws Exception {
        MessageSenderMock.LOGGER.info("[{}-{}]: {}", queue, correlation, InterconnectMapper.toJson(ico));
        this.broker.send(queue, false, ico, headers);
    }

}
