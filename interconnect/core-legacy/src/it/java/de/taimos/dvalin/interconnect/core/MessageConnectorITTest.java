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

import java.util.HashMap;
import java.util.UUID;

import javax.jms.TextMessage;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.taimos.dvalin.interconnect.core.exceptions.TimeoutException;
import de.taimos.dvalin.interconnect.model.InterconnectConstants;


/**
 * This integration tests needs an ActiveMQ instance (BROKER_URL) to work!
 */
@SuppressWarnings("javadoc")
public final class MessageConnectorITTest {

    @BeforeClass
    public static void setUp() {
        System.setProperty(InterconnectConstants.PROPERTY_CRYPTO_AESKEY, "4b5c6acc6cedc3093d7ad49d195af14a");
        System.setProperty(InterconnectConstants.PROPERTY_CRYPTO_SIGNATURE, "8602266778973c0edd198713985b9e56");
        TestHelper.initBrokerEnv("failover:tcp://localhost:61616");
    }

    @AfterClass
    public static void tearDown() {
        TestHelper.closeBrokerEnv();
    }


    private String queueName;


    @Before
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
                    Assert.assertEquals("ping", tm.getText());
                    MessageConnector.sendToDestination(tm.getJMSReplyTo(), "pong", new HashMap<String, Object>(), false, null, tm.getJMSCorrelationID());
                } catch (final Exception e) {
                    Assert.fail("Exception");
                }
            }
        }).start();
        final TextMessage res = MessageConnector.request(q, "ping", new HashMap<String, Object>());
        Assert.assertEquals("pong", res.getText());
    }

    @Test
    public void testEncryptedRequest() throws Exception {
        final String q = this.queueName;
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    final TextMessage tm = MessageConnector.receiveFromQueue(q, "", 5000, false);
                    Assert.assertNotEquals("ping", tm.getText());
                    MessageConnector.decryptMessage(tm);
                    Assert.assertEquals("ping", tm.getText());
                    MessageConnector.sendToDestination(tm.getJMSReplyTo(), "pong", new HashMap<String, Object>(), true, null, tm.getJMSCorrelationID());
                } catch (final Exception e) {
                    Assert.fail("Exception");
                }
            }
        }).start();
        final TextMessage res = MessageConnector.request(q, "ping", new HashMap<String, Object>(), true, MessageConnector.REQUEST_TIMEOUT, MessageConnector.REQUEST_TIMEOUT, MessageConnector.MSGPRIORITY);
        Assert.assertEquals("pong", res.getText());
    }

    @Test(timeout = 11000, expected = TimeoutException.class)
    public void testDefaultTimeoutRequest() throws Exception {
        MessageConnector.request(this.queueName, "ping", new HashMap<String, Object>());
    }

    @Test(timeout = 2000, expected = TimeoutException.class)
    public void testCustomTimeoutRequest() throws Exception {
        MessageConnector.request(this.queueName, "ping", new HashMap<String, Object>(), true, 1000, MessageConnector.REQUEST_TIMEOUT, MessageConnector.MSGPRIORITY);
    }

    @Test
    public void testSendAndReceive() throws Exception {
        MessageConnector.sendToQueue(this.queueName, "ping", new HashMap<String, Object>());
        final TextMessage res = MessageConnector.receiveFromQueue(this.queueName, null, 1000, false);
        Assert.assertEquals("ping", res.getText());
        Assert.assertNull(res.getJMSReplyTo());
        Assert.assertNull(res.getJMSCorrelationID());
    }

    @Test
    public void testSecureSendAndReceive() throws Exception {
        MessageConnector.sendToQueue(this.queueName, "ping", new HashMap<String, Object>(), true);
        final TextMessage res = MessageConnector.receiveFromQueue(this.queueName, null, 1000, false);
        Assert.assertNotEquals("ping", res.getText());
        MessageConnector.decryptMessage(res);
        Assert.assertEquals("ping", res.getText());
        Assert.assertNull(res.getJMSReplyTo());
        Assert.assertNull(res.getJMSCorrelationID());
    }

    @Test
    public void testSecureSendAndReceiveWithReplyTo() throws Exception {
        MessageConnector.sendToQueue(this.queueName, "ping", new HashMap<String, Object>(), "test", null);
        final TextMessage res = MessageConnector.receiveFromQueue(this.queueName, null, 1000, false);
        Assert.assertEquals("ping", res.getText());
        Assert.assertNotNull(res.getJMSReplyTo());
        Assert.assertEquals("queue://test", res.getJMSReplyTo().toString());
        Assert.assertNull(res.getJMSCorrelationID());
    }

    @Test
    public void testSecureSendAndReceiveWithCorrelationId() throws Exception {
        MessageConnector.sendToQueue(this.queueName, "ping", new HashMap<String, Object>(), null, "test123");
        final TextMessage res = MessageConnector.receiveFromQueue(this.queueName, null, 1000, false);
        Assert.assertEquals("ping", res.getText());
        Assert.assertNull(res.getJMSReplyTo());
        Assert.assertNotNull(res.getJMSCorrelationID());
        Assert.assertEquals("test123", res.getJMSCorrelationID());
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
                    Assert.fail("Exception");
                }
            }
        }).start();
        Assert.assertEquals(5, MessageConnector.receiveBulkFromQueue(q, null, 5, 1000, false).size());
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
                    Assert.fail("Exception");
                }
            }
        }).start();
        Assert.assertEquals(1, MessageConnector.receiveBulkFromQueue(q, null, 5, 1000, false).size());
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
                    Assert.fail("Exception");
                }
            }
        }).start();
        Assert.assertEquals(5, MessageConnector.receiveBulkFromQueue(q, null, 5, 1000, false).size());
        Assert.assertEquals(1, MessageConnector.receiveBulkFromQueue(q, null, 5, 1000, false).size());
    }

    @Test(expected = TimeoutException.class, timeout = 2000)
    public void testReceiveBulkFromQueue0of5() throws Exception {
        final String q = this.queueName;
        MessageConnector.receiveBulkFromQueue(q, null, 5, 1000, false).size();
    }

    @Test(expected = TimeoutException.class, timeout = 2000)
    public void testReceiveBulkFromTopic0of5() throws Exception {
        final String q = this.queueName;
        MessageConnector.receiveBulkFromTopic(q, null, 5, 1000, false).size();
    }
}
