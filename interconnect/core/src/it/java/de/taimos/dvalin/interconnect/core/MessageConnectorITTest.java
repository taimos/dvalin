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

import java.time.Duration;
import java.util.HashMap;
import java.util.UUID;

import javax.jms.TextMessage;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.taimos.dvalin.interconnect.core.exceptions.TimeoutException;
import de.taimos.dvalin.interconnect.model.InterconnectConstants;


/**
 * This integration tests needs an ActiveMQ instance (BROKER_URL) to work!
 */
@SuppressWarnings("javadoc")
public final class MessageConnectorITTest {

    @BeforeAll
    public static void setUp() {
        System.setProperty(InterconnectConstants.PROPERTY_CRYPTO_AESKEY, "4b5c6acc6cedc3093d7ad49d195af14a");
        System.setProperty(InterconnectConstants.PROPERTY_CRYPTO_SIGNATURE, "8602266778973c0edd198713985b9e56");
        de.taimos.dvalin.interconnect.core.TestHelper.initBrokerEnv("failover:tcp://localhost:61616");
    }

    @AfterAll
    public static void tearDown() {
        de.taimos.dvalin.interconnect.core.TestHelper.closeBrokerEnv();
    }


    private String queueName;


    @BeforeEach
    public void init() {
        this.queueName = "test." + this.getClass().getSimpleName() + "." + UUID.randomUUID();
    }

    @Test
    public void testSimpleRequest() throws Exception {
        final String q = this.queueName;
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    final TextMessage tm = MessageConnector.receiveFromQueue(q, "", 5000, false);
                    Assertions.assertEquals("ping", tm.getText());
                    MessageConnector.sendToDestination(tm.getJMSReplyTo(), "pong", new HashMap<>(), false, null, tm.getJMSCorrelationID());
                } catch (final Exception e) {
                    Assertions.fail("Exception");
                }
            }
        }).start();
        final TextMessage res = MessageConnector.request(q, "ping", new HashMap<>());
        Assertions.assertEquals("pong", res.getText());
    }

    @Test
    public void testEncryptedRequest() throws Exception {
        final String q = this.queueName;
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    final TextMessage tm = MessageConnector.receiveFromQueue(q, "", 5000, false);
                    Assertions.assertNotEquals("ping", tm.getText());
                    MessageConnector.decryptMessage(tm);
                    Assertions.assertEquals("ping", tm.getText());
                    MessageConnector.sendToDestination(tm.getJMSReplyTo(), "pong", new HashMap<>(), true, null, tm.getJMSCorrelationID());
                } catch (final Exception e) {
                    Assertions.fail("Exception");
                }
            }
        }).start();
        final TextMessage res = MessageConnector.request(q, "ping", new HashMap<>(), true, MessageConnector.REQUEST_TIMEOUT, MessageConnector.REQUEST_TIMEOUT, MessageConnector.MSGPRIORITY);
        Assertions.assertEquals("pong", res.getText());
    }

    @Test
    public void testDefaultTimeoutRequest() throws Exception {
        Assertions.assertThrows(TimeoutException.class, () -> Assertions.assertTimeout(Duration.ofMillis(11000), () ->
		MessageConnector.request(this.queueName, "ping", new HashMap<>())));
    }

    @Test
    public void testCustomTimeoutRequest() throws Exception {
        Assertions.assertThrows(TimeoutException.class, () -> Assertions.assertTimeout(Duration.ofMillis(2000), () ->
            MessageConnector.request(this.queueName, "ping", new HashMap<>(), true, 1000, MessageConnector.REQUEST_TIMEOUT, MessageConnector.MSGPRIORITY)));
    }

    @Test
    public void testSendAndReceive() throws Exception {
        MessageConnector.sendToQueue(this.queueName, "ping", new HashMap<>());
        final TextMessage res = MessageConnector.receiveFromQueue(this.queueName, null, 1000, false);
        Assertions.assertEquals("ping", res.getText());
        Assertions.assertNull(res.getJMSReplyTo());
        Assertions.assertNull(res.getJMSCorrelationID());
    }

    @Test
    public void testSecureSendAndReceive() throws Exception {
        MessageConnector.sendToQueue(this.queueName, "ping", new HashMap<>(), true);
        final TextMessage res = MessageConnector.receiveFromQueue(this.queueName, null, 1000, false);
        Assertions.assertNotEquals("ping", res.getText());
        MessageConnector.decryptMessage(res);
        Assertions.assertEquals("ping", res.getText());
        Assertions.assertNull(res.getJMSReplyTo());
        Assertions.assertNull(res.getJMSCorrelationID());
    }

    @Test
    public void testSecureSendAndReceiveWithReplyTo() throws Exception {
        MessageConnector.sendToQueue(this.queueName, "ping", new HashMap<>(), "test", null);
        final TextMessage res = MessageConnector.receiveFromQueue(this.queueName, null, 1000, false);
        Assertions.assertEquals("ping", res.getText());
        Assertions.assertNotNull(res.getJMSReplyTo());
        Assertions.assertEquals("queue://test", res.getJMSReplyTo().toString());
        Assertions.assertNull(res.getJMSCorrelationID());
    }

    @Test
    public void testSecureSendAndReceiveWithCorrelationId() throws Exception {
        MessageConnector.sendToQueue(this.queueName, "ping", new HashMap<>(), null, "test123");
        final TextMessage res = MessageConnector.receiveFromQueue(this.queueName, null, 1000, false);
        Assertions.assertEquals("ping", res.getText());
        Assertions.assertNull(res.getJMSReplyTo());
        Assertions.assertNotNull(res.getJMSCorrelationID());
        Assertions.assertEquals("test123", res.getJMSCorrelationID());
    }

    @Test
    public void testReceiveBulkFromQueue5of5() throws Exception {
        final String q = this.queueName;
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    MessageConnector.sendToQueue(MessageConnectorITTest.this.queueName, "pong", null);
                    MessageConnector.sendToQueue(MessageConnectorITTest.this.queueName, "pong", null);
                    MessageConnector.sendToQueue(MessageConnectorITTest.this.queueName, "pong", null);
                    MessageConnector.sendToQueue(MessageConnectorITTest.this.queueName, "pong", null);
                    MessageConnector.sendToQueue(MessageConnectorITTest.this.queueName, "pong", null);
                } catch (final Exception e) {
                    Assertions.fail("Exception");
                }
            }
        }).start();
        Assertions.assertEquals(5, MessageConnector.receiveBulkFromQueue(q, null, 5, 1000, false).size());
    }

    @Test
    public void testReceiveBulkFromQueue1of5() throws Exception {
        final String q = this.queueName;
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    MessageConnector.sendToQueue(MessageConnectorITTest.this.queueName, "pong", null);
                } catch (final Exception e) {
                    Assertions.fail("Exception");
                }
            }
        }).start();
        Assertions.assertEquals(1, MessageConnector.receiveBulkFromQueue(q, null, 5, 1000, false).size());
    }

    @Test
    public void testReceiveBulkFromQueue5of6() throws Exception {
        final String q = this.queueName;
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    MessageConnector.sendToQueue(MessageConnectorITTest.this.queueName, "pong", null);
                    MessageConnector.sendToQueue(MessageConnectorITTest.this.queueName, "pong", null);
                    MessageConnector.sendToQueue(MessageConnectorITTest.this.queueName, "pong", null);
                    MessageConnector.sendToQueue(MessageConnectorITTest.this.queueName, "pong", null);
                    MessageConnector.sendToQueue(MessageConnectorITTest.this.queueName, "pong", null);
                    MessageConnector.sendToQueue(MessageConnectorITTest.this.queueName, "pong", null);
                } catch (final Exception e) {
                    Assertions.fail("Exception");
                }
            }
        }).start();
        Assertions.assertEquals(5, MessageConnector.receiveBulkFromQueue(q, null, 5, 1000, false).size());
        Assertions.assertEquals(1, MessageConnector.receiveBulkFromQueue(q, null, 5, 1000, false).size());
    }

    @Test
    public void testReceiveBulkFromQueue0of5() throws Exception {
        final String q = this.queueName;
        Assertions.assertThrows(TimeoutException.class, () -> {
            Assertions.assertTimeout(Duration.ofMillis(2000), () ->MessageConnector.receiveBulkFromQueue(q, null, 5, 1000, false).size());
        });
    }

    @Test
    public void testReceiveBulkFromTopic0of5() throws Exception {
        final String q = this.queueName;
        Assertions.assertThrows(TimeoutException.class, () -> {
            Assertions.assertTimeout(Duration.ofMillis(2000), () -> MessageConnector.receiveBulkFromTopic(q, null, 5, 1000, false).size());
        });
    }
}
