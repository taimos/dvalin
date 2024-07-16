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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.taimos.dvalin.interconnect.core.InterconnectConnector.Response;
import de.taimos.dvalin.interconnect.core.exceptions.TimeoutException;
import de.taimos.dvalin.interconnect.model.InterconnectConstants;
import de.taimos.dvalin.interconnect.model.InterconnectMapper;
import de.taimos.dvalin.interconnect.model.InterconnectObject;
import de.taimos.dvalin.interconnect.model.ivo.daemon.VoidIVO;
import de.taimos.dvalin.interconnect.model.ivo.daemon.VoidIVO.VoidIVOBuilder;


/**
 * This integration tests needs an ActiveMQ instance (BROKER_URL) to work!
 */
@SuppressWarnings("javadoc")
public final class InterconnectConnectorITTest {

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

    @AfterEach
    public void close() {
        //
    }

    @Test
    void testSimpleRequest() throws Exception {
        final String q = this.queueName;
        new Thread(() -> {
            try {
                final Response request = InterconnectConnector.receiveFromQueueEnhanced(q, "", 5000, false);
                Assertions.assertNotNull(request.getJMSTextMessage().getStringProperty(InterconnectConnector.HEADER_REQUEST_UUID));
                Assertions.assertNotNull(request.getJMSTextMessage().getStringProperty(InterconnectConnector.HEADER_ICO_CLASS));
                Assertions.assertEquals(VoidIVO.class, request.getICO().getClass());
                MessageConnector.sendToDestination(request.getJMSTextMessage().getJMSReplyTo(), InterconnectMapper.toJson(new VoidIVOBuilder().build()), new HashMap<String, Object>(), false, null, request.getJMSTextMessage().getJMSCorrelationID());
            } catch (final Exception e) {
                Assertions.fail("Exception");
            }
        }).start();
        final InterconnectObject res = InterconnectConnector.request(UUID.randomUUID(), q, new VoidIVOBuilder().build(), new HashMap<String, Object>());
        Assertions.assertEquals(VoidIVO.class, res.getClass());
    }

    @Test
    void testEncryptedRequest() throws Exception {
        final String q = this.queueName;
        new Thread(() -> {
            try {
                final TextMessage tm = MessageConnector.receiveFromQueue(q, "", 5000, false);
                final String json = InterconnectMapper.toJson(new VoidIVOBuilder().build());
                Assertions.assertNotNull(tm.getStringProperty(InterconnectConnector.HEADER_REQUEST_UUID));
                Assertions.assertNotNull(tm.getStringProperty(InterconnectConnector.HEADER_ICO_CLASS));
                Assertions.assertNotEquals(json, tm.getText());
                MessageConnector.decryptMessage(tm);
                Assertions.assertEquals(json, tm.getText());
                MessageConnector.sendToDestination(tm.getJMSReplyTo(), json, new HashMap<String, Object>(), true, null, tm.getJMSCorrelationID());
            } catch (final Exception e) {
                Assertions.fail("Exception");
            }
        }).start();
        final InterconnectObject res = InterconnectConnector.request(UUID.randomUUID(), q, new VoidIVOBuilder().build(), new HashMap<String, Object>(), true, MessageConnector.REQUEST_TIMEOUT, MessageConnector.REQUEST_TIMEOUT, MessageConnector.MSGPRIORITY);
        Assertions.assertEquals(VoidIVO.class, res.getClass());
    }

    @Test
    void testDefaultTimeoutRequest() {
        Assertions.assertThrows(TimeoutException.class, () -> {
            Assertions.assertTimeout(Duration.ofMillis(11000), () -> InterconnectConnector.request(UUID.randomUUID(), this.queueName, new VoidIVOBuilder().build(), new HashMap<String, Object>()));});
    }

    @Test
    void testCustomTimeoutRequest() {
        Assertions.assertThrows(TimeoutException.class, () -> {
            Assertions.assertTimeout(Duration.ofMillis(2000), () -> InterconnectConnector.request(UUID.randomUUID(), this.queueName, new VoidIVOBuilder().build(), new HashMap<String, Object>(), true, 1000, MessageConnector.REQUEST_TIMEOUT, MessageConnector.MSGPRIORITY));
        });
    }

    @Test
    void testSendAndReceive() throws Exception {
        InterconnectConnector.sendToQueue(this.queueName, new VoidIVOBuilder().build(), new HashMap<String, Object>());
        final Response res = InterconnectConnector.receiveFromQueueEnhanced(this.queueName, null, 1000, false);
        Assertions.assertEquals(VoidIVO.class, res.getICO().getClass());
        Assertions.assertNotNull(res.getJMSTextMessage().getStringProperty(InterconnectConnector.HEADER_ICO_CLASS));
        Assertions.assertNull(res.getJMSTextMessage().getJMSReplyTo());
        Assertions.assertNull(res.getJMSTextMessage().getJMSCorrelationID());
    }

    @Test
    void testSecureSendAndReceive() throws Exception {
        InterconnectConnector.sendToQueue(this.queueName, new VoidIVOBuilder().build(), new HashMap<String, Object>(), true);
        final Response res = InterconnectConnector.receiveFromQueueEnhanced(this.queueName, null, 1000, true);
        Assertions.assertEquals(VoidIVO.class, res.getICO().getClass());
        Assertions.assertNotNull(res.getJMSTextMessage().getStringProperty(InterconnectConnector.HEADER_ICO_CLASS));
        Assertions.assertNull(res.getJMSTextMessage().getJMSReplyTo());
        Assertions.assertNull(res.getJMSTextMessage().getJMSCorrelationID());
    }

    @Test
    void testSecureSendAndReceiveWithReplyTo() throws Exception {
        InterconnectConnector.sendToQueue(this.queueName, new VoidIVOBuilder().build(), new HashMap<String, Object>(), "test", null);
        final Response res = InterconnectConnector.receiveFromQueueEnhanced(this.queueName, null, 1000, false);
        Assertions.assertEquals(VoidIVO.class, res.getICO().getClass());
        Assertions.assertNotNull(res.getJMSTextMessage().getStringProperty(InterconnectConnector.HEADER_ICO_CLASS));
        Assertions.assertNotNull(res.getJMSTextMessage().getJMSReplyTo());
        Assertions.assertEquals("queue://test", res.getJMSTextMessage().getJMSReplyTo().toString());
        Assertions.assertNull(res.getJMSTextMessage().getJMSCorrelationID());
    }

    @Test
    void testSecureSendAndReceiveWithCorrelationId() throws Exception {
        InterconnectConnector.sendToQueue(this.queueName, new VoidIVOBuilder().build(), new HashMap<String, Object>(), null, "test123");
        final Response res = InterconnectConnector.receiveFromQueueEnhanced(this.queueName, null, 1000, false);
        Assertions.assertEquals(VoidIVO.class, res.getICO().getClass());
        Assertions.assertNotNull(res.getJMSTextMessage().getStringProperty(InterconnectConnector.HEADER_ICO_CLASS));
        Assertions.assertNull(res.getJMSTextMessage().getJMSReplyTo());
        Assertions.assertNotNull(res.getJMSTextMessage().getJMSCorrelationID());
        Assertions.assertEquals("test123", res.getJMSTextMessage().getJMSCorrelationID());
    }

    @Test
    void testReceiveBulkFromQueue5of5() throws Exception {
        final String q = this.queueName;
        new Thread(() -> {
            try {
                InterconnectConnector.sendToQueue(InterconnectConnectorITTest.this.queueName, new VoidIVOBuilder().build(), null);
                InterconnectConnector.sendToQueue(InterconnectConnectorITTest.this.queueName, new VoidIVOBuilder().build(), null);
                InterconnectConnector.sendToQueue(InterconnectConnectorITTest.this.queueName, new VoidIVOBuilder().build(), null);
                InterconnectConnector.sendToQueue(InterconnectConnectorITTest.this.queueName, new VoidIVOBuilder().build(), null);
                InterconnectConnector.sendToQueue(InterconnectConnectorITTest.this.queueName, new VoidIVOBuilder().build(), null);
            } catch (final Exception e) {
                Assertions.fail("Exception");
            }
        }).start();
        Assertions.assertEquals(5, InterconnectConnector.receiveBulkFromQueue(q, null, 5, 1000, false).size());
    }

    @Test
    void testReceiveBulkFromQueue1of5() throws Exception {
        final String q = this.queueName;
        new Thread(() -> {
            try {
                InterconnectConnector.sendToQueue(InterconnectConnectorITTest.this.queueName, new VoidIVOBuilder().build(), null);
            } catch (final Exception e) {
                Assertions.fail("Exception");
            }
        }).start();
        Assertions.assertEquals(1, InterconnectConnector.receiveBulkFromQueue(q, null, 5, 1000, false).size());
    }

    @Test
    void testReceiveBulkFromQueue5of6() throws Exception {
        final String q = this.queueName;
        new Thread(() -> {
            try {
                InterconnectConnector.sendToQueue(InterconnectConnectorITTest.this.queueName, new VoidIVOBuilder().build(), null);
                InterconnectConnector.sendToQueue(InterconnectConnectorITTest.this.queueName, new VoidIVOBuilder().build(), null);
                InterconnectConnector.sendToQueue(InterconnectConnectorITTest.this.queueName, new VoidIVOBuilder().build(), null);
                InterconnectConnector.sendToQueue(InterconnectConnectorITTest.this.queueName, new VoidIVOBuilder().build(), null);
                InterconnectConnector.sendToQueue(InterconnectConnectorITTest.this.queueName, new VoidIVOBuilder().build(), null);
                InterconnectConnector.sendToQueue(InterconnectConnectorITTest.this.queueName, new VoidIVOBuilder().build(), null);
            } catch (final Exception e) {
                Assertions.fail("Exception");
            }
        }).start();
        Assertions.assertEquals(5, InterconnectConnector.receiveBulkFromQueue(q, null, 5, 1000, false).size());
        Assertions.assertEquals(1, InterconnectConnector.receiveBulkFromQueue(q, null, 5, 1000, false).size());
    }

    @Test
    void testReceiveBulkFromQueue0of5() {
        final String q = this.queueName;
        Assertions.assertThrows(TimeoutException.class, () -> {
            Assertions.assertTimeout(Duration.ofMillis(2000), () ->InterconnectConnector.receiveBulkFromQueue(q, null, 5, 1000, false).size());
        });
    }

    @Test
    void testReceiveBulkFromTopic0of5() {
        final String q = this.queueName;
        Assertions.assertThrows(TimeoutException.class, () -> {
            Assertions.assertTimeout(Duration.ofMillis(2000), () ->InterconnectConnector.receiveBulkFromTopic(q, null, 5, 1000, false).size());
        });
    }
}
