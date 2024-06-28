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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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

    @After
    public void close() throws Exception {
        //
    }

    @Test
    public void testSimpleRequest() throws Exception {
        final String q = this.queueName;
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    final Response request = InterconnectConnector.receiveFromQueueEnhanced(q, "", 5000, false);
                    Assert.assertNotNull(request.getJMSTextMessage().getStringProperty(InterconnectConnector.HEADER_REQUEST_UUID));
                    Assert.assertNotNull(request.getJMSTextMessage().getStringProperty(InterconnectConnector.HEADER_ICO_CLASS));
                    Assert.assertEquals(VoidIVO.class, request.getICO().getClass());
                    MessageConnector.sendToDestination(request.getJMSTextMessage().getJMSReplyTo(), InterconnectMapper.toJson(new VoidIVOBuilder().build()), new HashMap<String, Object>(), false, null, request.getJMSTextMessage().getJMSCorrelationID());
                } catch (final Exception e) {
                    Assert.fail("Exception");
                }
            }
        }).start();
        final InterconnectObject res = InterconnectConnector.request(UUID.randomUUID(), q, new VoidIVOBuilder().build(), new HashMap<String, Object>());
        Assert.assertEquals(VoidIVO.class, res.getClass());
    }

    @Test
    public void testEncryptedRequest() throws Exception {
        final String q = this.queueName;
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    final TextMessage tm = MessageConnector.receiveFromQueue(q, "", 5000, false);
                    final String json = InterconnectMapper.toJson(new VoidIVOBuilder().build());
                    Assert.assertNotNull(tm.getStringProperty(InterconnectConnector.HEADER_REQUEST_UUID));
                    Assert.assertNotNull(tm.getStringProperty(InterconnectConnector.HEADER_ICO_CLASS));
                    Assert.assertNotEquals(json, tm.getText());
                    MessageConnector.decryptMessage(tm);
                    Assert.assertEquals(json, tm.getText());
                    MessageConnector.sendToDestination(tm.getJMSReplyTo(), json, new HashMap<String, Object>(), true, null, tm.getJMSCorrelationID());
                } catch (final Exception e) {
                    Assert.fail("Exception");
                }
            }
        }).start();
        final InterconnectObject res = InterconnectConnector.request(UUID.randomUUID(), q, new VoidIVOBuilder().build(), new HashMap<String, Object>(), true, MessageConnector.REQUEST_TIMEOUT, MessageConnector.REQUEST_TIMEOUT, MessageConnector.MSGPRIORITY);
        Assert.assertEquals(VoidIVO.class, res.getClass());
    }

    @Test(timeout = 11000, expected = TimeoutException.class)
    public void testDefaultTimeoutRequest() throws Exception {
        InterconnectConnector.request(UUID.randomUUID(), this.queueName, new VoidIVOBuilder().build(), new HashMap<String, Object>());
    }

    @Test(timeout = 2000, expected = TimeoutException.class)
    public void testCustomTimeoutRequest() throws Exception {
        InterconnectConnector.request(UUID.randomUUID(), this.queueName, new VoidIVOBuilder().build(), new HashMap<String, Object>(), true, 1000, MessageConnector.REQUEST_TIMEOUT, MessageConnector.MSGPRIORITY);
    }

    @Test
    public void testSendAndReceive() throws Exception {
        InterconnectConnector.sendToQueue(this.queueName, new VoidIVOBuilder().build(), new HashMap<String, Object>());
        final Response res = InterconnectConnector.receiveFromQueueEnhanced(this.queueName, null, 1000, false);
        Assert.assertEquals(VoidIVO.class, res.getICO().getClass());
        Assert.assertNotNull(res.getJMSTextMessage().getStringProperty(InterconnectConnector.HEADER_ICO_CLASS));
        Assert.assertNull(res.getJMSTextMessage().getJMSReplyTo());
        Assert.assertNull(res.getJMSTextMessage().getJMSCorrelationID());
    }

    @Test
    public void testSecureSendAndReceive() throws Exception {
        InterconnectConnector.sendToQueue(this.queueName, new VoidIVOBuilder().build(), new HashMap<String, Object>(), true);
        final Response res = InterconnectConnector.receiveFromQueueEnhanced(this.queueName, null, 1000, true);
        Assert.assertEquals(VoidIVO.class, res.getICO().getClass());
        Assert.assertNotNull(res.getJMSTextMessage().getStringProperty(InterconnectConnector.HEADER_ICO_CLASS));
        Assert.assertNull(res.getJMSTextMessage().getJMSReplyTo());
        Assert.assertNull(res.getJMSTextMessage().getJMSCorrelationID());
    }

    @Test
    public void testSecureSendAndReceiveWithReplyTo() throws Exception {
        InterconnectConnector.sendToQueue(this.queueName, new VoidIVOBuilder().build(), new HashMap<String, Object>(), "test", null);
        final Response res = InterconnectConnector.receiveFromQueueEnhanced(this.queueName, null, 1000, false);
        Assert.assertEquals(VoidIVO.class, res.getICO().getClass());
        Assert.assertNotNull(res.getJMSTextMessage().getStringProperty(InterconnectConnector.HEADER_ICO_CLASS));
        Assert.assertNotNull(res.getJMSTextMessage().getJMSReplyTo());
        Assert.assertEquals("queue://test", res.getJMSTextMessage().getJMSReplyTo().toString());
        Assert.assertNull(res.getJMSTextMessage().getJMSCorrelationID());
    }

    @Test
    public void testSecureSendAndReceiveWithCorrelationId() throws Exception {
        InterconnectConnector.sendToQueue(this.queueName, new VoidIVOBuilder().build(), new HashMap<String, Object>(), null, "test123");
        final Response res = InterconnectConnector.receiveFromQueueEnhanced(this.queueName, null, 1000, false);
        Assert.assertEquals(VoidIVO.class, res.getICO().getClass());
        Assert.assertNotNull(res.getJMSTextMessage().getStringProperty(InterconnectConnector.HEADER_ICO_CLASS));
        Assert.assertNull(res.getJMSTextMessage().getJMSReplyTo());
        Assert.assertNotNull(res.getJMSTextMessage().getJMSCorrelationID());
        Assert.assertEquals("test123", res.getJMSTextMessage().getJMSCorrelationID());
    }

    @Test
    public void testReceiveBulkFromQueue5of5() throws Exception {
        final String q = this.queueName;
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    InterconnectConnector.sendToQueue(InterconnectConnectorITTest.this.queueName, new VoidIVOBuilder().build(), null);
                    InterconnectConnector.sendToQueue(InterconnectConnectorITTest.this.queueName, new VoidIVOBuilder().build(), null);
                    InterconnectConnector.sendToQueue(InterconnectConnectorITTest.this.queueName, new VoidIVOBuilder().build(), null);
                    InterconnectConnector.sendToQueue(InterconnectConnectorITTest.this.queueName, new VoidIVOBuilder().build(), null);
                    InterconnectConnector.sendToQueue(InterconnectConnectorITTest.this.queueName, new VoidIVOBuilder().build(), null);
                } catch (final Exception e) {
                    Assert.fail("Exception");
                }
            }
        }).start();
        Assert.assertEquals(5, InterconnectConnector.receiveBulkFromQueue(q, null, 5, 1000, false).size());
    }

    @Test
    public void testReceiveBulkFromQueue1of5() throws Exception {
        final String q = this.queueName;
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    InterconnectConnector.sendToQueue(InterconnectConnectorITTest.this.queueName, new VoidIVOBuilder().build(), null);
                } catch (final Exception e) {
                    Assert.fail("Exception");
                }
            }
        }).start();
        Assert.assertEquals(1, InterconnectConnector.receiveBulkFromQueue(q, null, 5, 1000, false).size());
    }

    @Test
    public void testReceiveBulkFromQueue5of6() throws Exception {
        final String q = this.queueName;
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    InterconnectConnector.sendToQueue(InterconnectConnectorITTest.this.queueName, new VoidIVOBuilder().build(), null);
                    InterconnectConnector.sendToQueue(InterconnectConnectorITTest.this.queueName, new VoidIVOBuilder().build(), null);
                    InterconnectConnector.sendToQueue(InterconnectConnectorITTest.this.queueName, new VoidIVOBuilder().build(), null);
                    InterconnectConnector.sendToQueue(InterconnectConnectorITTest.this.queueName, new VoidIVOBuilder().build(), null);
                    InterconnectConnector.sendToQueue(InterconnectConnectorITTest.this.queueName, new VoidIVOBuilder().build(), null);
                    InterconnectConnector.sendToQueue(InterconnectConnectorITTest.this.queueName, new VoidIVOBuilder().build(), null);
                } catch (final Exception e) {
                    Assert.fail("Exception");
                }
            }
        }).start();
        Assert.assertEquals(5, InterconnectConnector.receiveBulkFromQueue(q, null, 5, 1000, false).size());
        Assert.assertEquals(1, InterconnectConnector.receiveBulkFromQueue(q, null, 5, 1000, false).size());
    }

    @Test(expected = TimeoutException.class, timeout = 2000)
    public void testReceiveBulkFromQueue0of5() throws Exception {
        final String q = this.queueName;
        InterconnectConnector.receiveBulkFromQueue(q, null, 5, 1000, false).size();
    }

    @Test(expected = TimeoutException.class, timeout = 2000)
    public void testReceiveBulkFromTopic0of5() throws Exception {
        final String q = this.queueName;
        InterconnectConnector.receiveBulkFromTopic(q, null, 5, 1000, false).size();
    }
}
