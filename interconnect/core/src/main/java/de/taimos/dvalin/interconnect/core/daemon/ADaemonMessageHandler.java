package de.taimos.dvalin.interconnect.core.daemon;

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

import de.taimos.dvalin.interconnect.core.daemon.DaemonMethodRegistry.RegistryEntry;
import de.taimos.dvalin.interconnect.core.model.DvalinInterconnectJmsSendObject;
import de.taimos.dvalin.interconnect.model.InterconnectContext;
import de.taimos.dvalin.interconnect.model.InterconnectMapper;
import de.taimos.dvalin.interconnect.model.InterconnectObject;
import de.taimos.dvalin.interconnect.model.ivo.IPageable;
import de.taimos.dvalin.interconnect.model.ivo.IVO;
import de.taimos.dvalin.interconnect.model.ivo.daemon.DaemonErrorIVO;
import de.taimos.dvalin.interconnect.model.ivo.daemon.PingIVO;
import de.taimos.dvalin.interconnect.model.ivo.daemon.PongIVO;
import de.taimos.dvalin.interconnect.model.service.DaemonError;
import de.taimos.dvalin.interconnect.model.service.DaemonErrorNumber;
import de.taimos.dvalin.interconnect.model.service.DaemonScanner;
import de.taimos.dvalin.interconnect.model.service.DaemonScanner.DaemonMethod;
import de.taimos.dvalin.interconnect.model.service.IDaemonHandler;
import de.taimos.dvalin.jms.crypto.ICryptoService;
import de.taimos.dvalin.jms.exceptions.MessageCryptoException;
import org.slf4j.Logger;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.UUID;

/**
 * Copyright 2015 Taimos GmbH<br>
 * <br>
 *
 * @author Thorsten Hoeger
 */
public abstract class ADaemonMessageHandler {

    protected final DaemonMethodRegistry registry;

    private final boolean throwExceptionOnTimeout;

    private final ICryptoService cryptoService;

    /**
     * @param aHandlerClazz            Handler class
     * @param aThrowExceptionOnTimeout if true throw Exception if timeout is reached
     */
    protected ADaemonMessageHandler(final Class<? extends IDaemonHandler> aHandlerClazz, ICryptoService cryptoService, final boolean aThrowExceptionOnTimeout) {
        this.registry = new DaemonMethodRegistry(aHandlerClazz);
        this.throwExceptionOnTimeout = aThrowExceptionOnTimeout;
        this.cryptoService = cryptoService;
    }

    /**
     * @param aHandlerClazzes          Handler classes
     * @param aThrowExceptionOnTimeout if true throw Exception if timeout is reached
     */
    protected ADaemonMessageHandler(final Collection<Class<? extends IDaemonHandler>> aHandlerClazzes, ICryptoService cryptoService, final boolean aThrowExceptionOnTimeout) {
        this.registry = new DaemonMethodRegistry(aHandlerClazzes);
        this.throwExceptionOnTimeout = aThrowExceptionOnTimeout;
        this.cryptoService = cryptoService;
    }

    /**
     * Reply with a Daemon response.
     *
     * @param response Response
     * @param secure   (encrypted communication)
     * @throws Exception If something went wrong
     */
    protected abstract void reply(DaemonResponse response, boolean secure) throws Exception;

    /**
     * Create a new request handler.
     *
     * @param registryEntry the registry entry
     * @return ADaemonHandler
     */
    protected abstract IDaemonHandler createRequestHandler(RegistryEntry registryEntry);

    protected abstract Logger getLogger();

    /**
     * @param message Message
     * @throws Exception If no registered method was found for the incomming InterconnectObject or Insecure call or no (valid) Request UUID
     *                   or no
     */
    public final void onMessage(final Message message) throws Exception {
        InterconnectContext.reset();

        final long begin = System.currentTimeMillis();
        if (message instanceof TextMessage) {
            final boolean secure = this.cryptoService.isMessageSecure((TextMessage) message);
            final TextMessage textMessage = this.getTextMessage((TextMessage) message, secure);

            final InterconnectObject ivoIn = InterconnectMapper.fromJson(textMessage.getText(),
                InterconnectObject.class);
            final Class<? extends InterconnectObject> icoClass = ivoIn.getClass();
            final DaemonRequest request = new DaemonRequest(textMessage.getJMSCorrelationID(),
                textMessage.getJMSReplyTo(), ivoIn);

            if (icoClass.equals(PingIVO.class)) {
                this.reply(new DaemonResponse(request, new PongIVO.PongIVOBuilder().build()), secure);
                return;
            }

            final RegistryEntry registryEntry = this.registry.get(icoClass);
            if (registryEntry == null) {
                throw new Exception(
                    "No registered method found for " + icoClass.getSimpleName() + " from " + message.getJMSReplyTo());
            }
            final DaemonScanner.DaemonMethod method = registryEntry.getMethod();
            if (method.isSecure() != secure) {
                throw new Exception("Insecure call (is " + secure + " should be " + method.isSecure() + ") for " +
                                    icoClass.getSimpleName() + " from " + message.getJMSReplyTo());
            }

            InterconnectContext.setUuid(ADaemonMessageHandler.getUuid(message, icoClass));
            InterconnectContext.setDeliveryCount(this.getDeliveryCount(message));
            InterconnectContext.setRedelivered(message.getJMSRedelivered());

            Class<? extends IVO> ivoClass;
            if (ivoIn instanceof IVO) {
                ivoClass = ADaemonMessageHandler.uncheckedCast(ivoIn);
                InterconnectContext.setRequestClass(ivoClass);
            }
            final IDaemonHandler handler = this.createRequestHandler(registryEntry);

            this.logInvoke(method, icoClass, ivoIn);

            if (method.getType() == DaemonScanner.Type.voit) {
                this.handleReceiver(handler, method, ivoIn);
            } else {
                try {
                    this.handleRequestAndReply(handler, method, ivoIn, request, begin, icoClass, secure);
                } catch (final DaemonError e) {
                    this.getLogger().debug(
                        "DaemonError for " + method.getMethod().getName() + "(" + icoClass.getSimpleName() + ")" +
                        " with " + InterconnectContext.getContext(), e);
                    final DaemonErrorIVO.DaemonErrorIVOBuilder error = new DaemonErrorIVO.DaemonErrorIVOBuilder();
                    error.number(e.getNumber().get());
                    error.daemon(e.getNumber().daemon());
                    error.message(e.getMessage());
                    this.reply(new DaemonResponse(request, error.build()), secure);
                }
            }
        } else {
            throw new Exception("Invalid message type received: " + message.getClass().getSimpleName());
        }
    }

    private void handleRequestAndReply(IDaemonHandler handler, DaemonMethod method, InterconnectObject ivoIn, DaemonRequest request, long begin, Class<? extends InterconnectObject> icoClass, boolean secure) throws Exception {
        final InterconnectObject out = this.handleRequest(handler, method, ivoIn);
        final DaemonResponse response = new DaemonResponse(request, out);
        final long end = System.currentTimeMillis();
        final long runtime = end - begin;
        if (runtime > method.getTimeoutInMs()) {
            if (this.throwExceptionOnTimeout) {
                throw new Exception("Response skipped because runtime " + runtime + " ms was greater than timeout " +
                                    method.getTimeoutInMs() + " ms for " + method.getMethod().getName() + "(" +
                                    icoClass.getSimpleName() + ")" + " with " + InterconnectContext.getContext());
            }
            this.getLogger().warn("Response skipped because runtime " + runtime + " ms was greater than timeout " +
                                  method.getTimeoutInMs() + " ms for " + method.getMethod().getName() + "(" +
                                  icoClass.getSimpleName() + ")" + " with " + InterconnectContext.getContext());
            return;
        } else if (runtime > (method.getTimeoutInMs() / 2L)) {
            this.getLogger().info(
                "Slow response because runtime " + runtime + " ms for " + method.getMethod().getName() + "(" +
                icoClass.getSimpleName() + ")" + " with " + InterconnectContext.getContext());
        }
        this.reply(response, secure);
    }

    private void logInvoke(DaemonMethod method, Class<? extends InterconnectObject> icoClass, InterconnectObject ivoIn) {
        final StringBuilder sbInvokeLog = new StringBuilder();
        sbInvokeLog.append("Invoke ") //
            .append(method.getMethod().getName()) //
            .append("(").append(icoClass.getSimpleName()).append(")");
        if (ivoIn instanceof IPageable) {
            sbInvokeLog.append(" at Page ").append(((IPageable) ivoIn).getOffset()).append(";")
                .append(((IPageable) ivoIn).getLimit());
        }
        sbInvokeLog.append(" with ").append(InterconnectContext.getContext());
        this.getLogger().info(sbInvokeLog.toString());
    }

    private int getDeliveryCount(Message message) throws JMSException {
        int deliveryCount;
        try {
            deliveryCount = message.getIntProperty("JMSXDeliveryCount");
        } catch (final Exception e) {
            if (message.getJMSRedelivered()) {
                deliveryCount = 2;
            } else {
                deliveryCount = 1;
            }
            this.getLogger().warn("Can not get JMSXDeliveryCount");
        }
        return deliveryCount;
    }

    private static UUID getUuid(Message message, Class<? extends InterconnectObject> icoClass) throws Exception {
        final String requestUUID = message.getStringProperty(DvalinInterconnectJmsSendObject.HEADER_REQUEST_UUID);
        if (requestUUID == null) {
            throw new Exception("No request UUID found in message with " + icoClass.getSimpleName() + " from " +
                                message.getJMSReplyTo());
        }
        try {
            return UUID.fromString(requestUUID);
        } catch (final IllegalArgumentException e) {
            throw new Exception(
                "No valid request UUID " + requestUUID + " message with " + icoClass.getSimpleName() + " from " +
                message.getJMSReplyTo());
        }
    }

    private TextMessage getTextMessage(TextMessage message, boolean secure) throws JMSException, MessageCryptoException {
        final TextMessage textMessage;
        this.getLogger().debug("TextMessage received: {}", message.getText());
        if (secure) {
            textMessage = this.cryptoService.decryptMessage(message);
        } else {
            textMessage = message;
        }
        return textMessage;
    }


    @SuppressWarnings("unchecked")
    private static Class<? extends IVO> uncheckedCast(InterconnectObject ivoIn) {
        return (Class<? extends IVO>) ivoIn.getClass();
    }

    private static Throwable extractTargetException(final InvocationTargetException e) {
        if (e.getTargetException() != null) {
            return e.getTargetException();
        }
        return e;
    }

    /**
     * @param handler Handler
     * @param method  Method
     * @param ico     Request
     * @return Response
     * @throws DaemonError Forward...
     */
    private InterconnectObject handleRequest(final IDaemonHandler handler, final DaemonScanner.DaemonMethod method, final InterconnectObject ico) throws DaemonError {
        handler.beforeRequestHook();
        try {
            return method.invoke(handler, ico);
        } catch (final InvocationTargetException e) {
            if (e.getTargetException() instanceof DaemonError) {
                throw (DaemonError) e.getTargetException();
            }
            if (e.getTargetException() instanceof RuntimeException) {
                handler.exceptionHook((RuntimeException) e.getTargetException());
            }
            final Throwable targetException = ADaemonMessageHandler.extractTargetException(e);
            if (method.isIdempotent()) {
                throw new IdemponentRetryException(targetException);
            }
            this.getLogger().error(
                "Exception in non-idempotent " + method.getMethod().getName() + "(" + ico.getClass().getSimpleName() +
                ")" + " with " + InterconnectContext.getContext(), e);
            throw new DaemonError(new DaemonErrorNumber() {

                private static final long serialVersionUID = 1L;


                @Override
                public int get() {
                    return -1;
                }

                @Override
                public String daemon() {
                    return "framework";
                }
            }, targetException);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            handler.afterRequestHook();
        }
    }

    /**
     * @param handler Handler
     * @param method  Method
     * @param ico     Receiver
     */
    private void handleReceiver(final IDaemonHandler handler, final DaemonScanner.DaemonMethod method, final InterconnectObject ico) {
        handler.beforeRequestHook();
        try {
            method.invoke(handler, ico);
        } catch (final InvocationTargetException e) {
            final Throwable targetException = ADaemonMessageHandler.extractTargetException(e);
            if (method.isIdempotent()) {
                throw new IdemponentRetryException(targetException);
            }
            throw new RuntimeException(targetException);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            handler.afterRequestHook();
        }
    }

}
