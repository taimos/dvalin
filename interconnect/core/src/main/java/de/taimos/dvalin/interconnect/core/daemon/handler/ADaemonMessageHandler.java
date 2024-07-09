package de.taimos.dvalin.interconnect.core.daemon.handler;

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

import de.taimos.dvalin.interconnect.core.daemon.IDaemonMessageHandler;
import de.taimos.dvalin.interconnect.core.daemon.IDaemonMessageSender;
import de.taimos.dvalin.interconnect.core.daemon.exceptions.FrameworkErrors;
import de.taimos.dvalin.interconnect.core.daemon.exceptions.IdemponentRetryException;
import de.taimos.dvalin.interconnect.core.daemon.model.HandlingDuration;
import de.taimos.dvalin.interconnect.core.daemon.model.HandlingDurationType;
import de.taimos.dvalin.interconnect.core.daemon.model.InterconnectContext;
import de.taimos.dvalin.interconnect.core.daemon.model.InterconnectResponseContext;
import de.taimos.dvalin.interconnect.core.daemon.util.DaemonMethodRegistry;
import de.taimos.dvalin.interconnect.core.daemon.util.DaemonMethodRegistry.RegistryEntry;
import de.taimos.dvalin.interconnect.model.InterconnectObject;
import de.taimos.dvalin.interconnect.model.ivo.IPageable;
import de.taimos.dvalin.interconnect.model.ivo.IVO;
import de.taimos.dvalin.interconnect.model.ivo.daemon.DaemonErrorIVO;
import de.taimos.dvalin.interconnect.model.ivo.daemon.PingIVO;
import de.taimos.dvalin.interconnect.model.ivo.daemon.PongIVO;
import de.taimos.dvalin.interconnect.model.service.DaemonError;
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
public abstract class ADaemonMessageHandler implements IDaemonMessageHandler {

    protected final DaemonMethodRegistry registry;
    protected final ICryptoService cryptoService;
    protected final IDaemonMessageSender messageSender;

    private final boolean throwExceptionOnTimeout;


    protected ADaemonMessageHandler(final Collection<Class<? extends IDaemonHandler>> aHandlerClazzes, IDaemonMessageSender messageSender, ICryptoService cryptoService, final boolean aThrowExceptionOnTimeout) {
        this.registry = new DaemonMethodRegistry(aHandlerClazzes);
        this.throwExceptionOnTimeout = aThrowExceptionOnTimeout;
        this.cryptoService = cryptoService;
        this.messageSender = messageSender;
    }


    /**
     * Create a new request handler.
     *
     * @param registryEntry the registry entry
     * @return ADaemonHandler
     */
    protected abstract IDaemonHandler createRequestHandler(RegistryEntry registryEntry);

    /**
     * @return the logger
     */
    protected abstract Logger getLogger();

    /**
     * Reply with a Daemon responseContext.
     *
     * @param response Response
     * @throws Exception If something went wrong
     */
    protected void reply(final InterconnectResponseContext response) throws Exception {
        this.messageSender.sendRequest(response.createResponseContext());
    }

    /**
     * @param message Message
     * @throws Exception If no registered method was found for the incomming InterconnectObject or Insecure call or no (valid) Request UUID
     *                   or no
     */
    @Override
    public final void onMessage(final Message message) throws Exception {
        de.taimos.dvalin.interconnect.model.InterconnectContext.reset();

        final long begin = System.currentTimeMillis();

        if (!(message instanceof TextMessage)) {
            throw new Exception("Invalid message type received: " + message.getClass().getSimpleName());
        }

        boolean secure = this.cryptoService.isMessageSecure(message);
        TextMessage receivedTextMessage = (TextMessage) this.decryptIfNecessary(this.cryptoService, message, secure);
        InterconnectResponseContext context = new InterconnectResponseContext(receivedTextMessage, secure, begin);

        if (context.getReceivedContext().getIcoClass().equals(PingIVO.class)) {
            this.handlePing(context);
            return;
        }

        this.updateThreadContext(context);

        final RegistryEntry registryEntry = this.getRegistryEntry(context);

        final IDaemonHandler handler = this.createRequestHandler(registryEntry);

        final DaemonMethod method = ADaemonMessageHandler.getDaemonMethod(registryEntry, context);
        context.setCreateResponseMethod(method);

        this.logInvoke(context);

        if (method.getType() == DaemonScanner.Type.voit) {
            this.handleWithoutReply(handler, context);
        } else {
            this.handleWithReply(handler, context);
        }
    }

    private Message decryptIfNecessary(ICryptoService cryptoService, Message message, boolean secure) throws MessageCryptoException {
        if (secure) {
            return cryptoService.decryptMessage(message);
        }
        return message;
    }

    private void handleWithReply(IDaemonHandler handler, InterconnectResponseContext context) throws Exception {
        try {
            final InterconnectObject responseIco = this.handleRequest(handler, context.getCreateResponseMethod(),
                context.getReceivedContext().getRequestIco());
            context.setResponseICO(responseIco);
            if (this.duration(context) == HandlingDurationType.TIMEOUT) {
                return;
            }
            this.reply(context);
        } catch (final DaemonError e) {
            this.getLogger().debug("DaemonError for " + context.getCreateResponseMethod().getMethod().getName() + "(" +
                                   context.getReceivedContext().getIcoClass().getSimpleName() + ")" + " with " +
                                   de.taimos.dvalin.interconnect.model.InterconnectContext.getContext(), e);
            this.sendErrorResponse(e, context);
        }
    }

    private void sendErrorResponse(DaemonError e, InterconnectResponseContext context) throws Exception {
        final DaemonErrorIVO.DaemonErrorIVOBuilder error = new DaemonErrorIVO.DaemonErrorIVOBuilder();
        error.number(e.getNumber().get());
        error.daemon(e.getNumber().daemon());
        error.message(e.getMessage());

        context.setResponseICO(error.build());
        this.reply(context);
    }

    private void updateThreadContext(InterconnectResponseContext context) throws Exception {
        de.taimos.dvalin.interconnect.model.InterconnectContext.setUuid(
            ADaemonMessageHandler.getUuid(context.getReceivedMessage(),
                context.getReceivedContext().getIcoClass()));
        de.taimos.dvalin.interconnect.model.InterconnectContext.setDeliveryCount(
            this.getDeliveryCount(context.getReceivedMessage()));
        de.taimos.dvalin.interconnect.model.InterconnectContext.setRedelivered(
            context.getReceivedMessage().getJMSRedelivered());
        Class<? extends IVO> ivoClass;
        if (context.getReceivedContext() instanceof IVO) {
            ivoClass = ADaemonMessageHandler.uncheckedCast(context.getResponseICO());
            de.taimos.dvalin.interconnect.model.InterconnectContext.setRequestClass(ivoClass);
        }
    }

    private static DaemonMethod getDaemonMethod(RegistryEntry registryEntry, InterconnectResponseContext context) throws Exception {
        final DaemonMethod method = registryEntry.getMethod();
        if (method.isSecure() != context.getReceivedContext().isSecure()) {
            throw new Exception(
                "Insecure call (is " + context.getReceivedContext().isSecure() + " should be " + method.isSecure() +
                ") for " + context.getReceivedContext().getIcoClass().getSimpleName() + " from " +
                context.getReceivedMessage().getJMSReplyTo());
        }
        return method;
    }

    private RegistryEntry getRegistryEntry(InterconnectResponseContext context) throws Exception {
        final RegistryEntry registryEntry = this.registry.get(context.getReceivedContext().getIcoClass());
        if (registryEntry == null) {
            throw new Exception(
                "No registered method found for " + context.getReceivedContext().getIcoClass().getSimpleName() +
                " from " + context.getReceivedMessage().getJMSReplyTo());
        }
        return registryEntry;
    }

    private void handlePing(InterconnectResponseContext context) throws Exception {
        context.setResponseICO(new PongIVO.PongIVOBuilder().build());
        this.reply(context);
    }

    private HandlingDurationType duration(InterconnectResponseContext context) throws Exception {
        HandlingDuration handlingDuration = context.handlingDuration();
        switch (handlingDuration.getHandlingDurationType()) {
            case TIMEOUT:
                if (this.throwExceptionOnTimeout) {
                    throw new Exception(context.timeoutMessage());
                }
                this.getLogger().warn(context.timeoutMessage());
                break;
            case SLOW_RESPONSE:
                this.getLogger().info(context.slowResponseMessage());
                break;
            default:
                break;
        }
        return handlingDuration.getHandlingDurationType();
    }

    private void logInvoke(InterconnectResponseContext context) {
        if (this.getLogger().isInfoEnabled()) {
            final StringBuilder sbInvokeLog = new StringBuilder() //
                .append("Invoke ") //
                .append(context.getCreateResponseMethod().getMethod().getName()) //
                .append("(").append(context.getReceivedContext().getIcoClass().getSimpleName()).append(")");
            if (context.getReceivedContext().getRequestIco() instanceof IPageable) {
                sbInvokeLog //
                    .append(" at Page ").append(((IPageable) context.getReceivedContext().getRequestIco()).getOffset())
                    .append(";").append(((IPageable) context.getReceivedContext().getRequestIco()).getLimit());
            }
            sbInvokeLog.append(" with ").append(de.taimos.dvalin.interconnect.model.InterconnectContext.getContext());
            this.getLogger().info(sbInvokeLog.toString());
        }
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
        final String requestUUID = message.getStringProperty(InterconnectContext.HEADER_REQUEST_UUID);
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
                ")" + " with " + de.taimos.dvalin.interconnect.model.InterconnectContext.getContext(), e);
            throw new DaemonError(FrameworkErrors.FRAMEWORK_ERROR, targetException);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            handler.afterRequestHook();
        }
    }

    /**
     * @param handler Handler
     */
    private void handleWithoutReply(final IDaemonHandler handler, InterconnectResponseContext context) {
        handler.beforeRequestHook();
        try {
            context.getCreateResponseMethod().invoke(handler, context.getReceivedContext().getRequestIco());
        } catch (final InvocationTargetException e) {
            final Throwable targetException = ADaemonMessageHandler.extractTargetException(e);
            if (context.getCreateResponseMethod().isIdempotent()) {
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
