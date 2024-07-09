package de.taimos.dvalin.interconnect.core.daemon.model;

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


import de.taimos.dvalin.interconnect.core.daemon.model.InterconnectContext.InterconnectContextBuilder;
import de.taimos.dvalin.interconnect.model.InterconnectMapper;
import de.taimos.dvalin.interconnect.model.InterconnectObject;
import de.taimos.dvalin.interconnect.model.service.DaemonScanner.DaemonMethod;
import de.taimos.dvalin.jms.exceptions.InfrastructureException;
import de.taimos.dvalin.jms.model.JmsResponseContext;
import de.taimos.dvalin.jms.model.JmsTarget;

import javax.annotation.Nonnull;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.IOException;

/**
 * Interconnect specific responseContext
 *
 * @author psigloch, fzwirn
 */
public final class InterconnectResponseContext extends JmsResponseContext<TextMessage> {

    private final InterconnectContext receivedContext;

    private InterconnectObject responseICO;

    private final long executionStartTime;
    private DaemonMethod createResponseMethod;
    private Long lastHandlingRuntime;


    /**
     * @param message            received text message
     * @param secure             if this was a secure communication
     * @param executionStartTime used by {@link InterconnectResponseContext#handlingDuration()} to calculation the duration of response handling, will use {@link System#currentTimeMillis()} if null
     * @throws InfrastructureException in case of errors
     */
    public InterconnectResponseContext(@Nonnull TextMessage message, boolean secure, Long executionStartTime) throws InfrastructureException {
        super(message);
        this.executionStartTime = executionStartTime != null ? executionStartTime : System.currentTimeMillis();
        final InterconnectObject receivedIco = this.extractIco();
        InterconnectContextBuilder receivedContextBuilder = new InterconnectContextBuilder() //
            .withTarget(JmsTarget.RECEPTION_CONTEXT).withSecure(secure) //
            .withRequestICO(receivedIco) //
            .withCorrelationId(this.extractCorreationId()) //
            .withReplyToDestination(this.getReplyToDestination());
        this.receivedContext = receivedContextBuilder.build();
    }

    private Destination getReplyToDestination() throws InfrastructureException {
        try {
            return this.getReceivedMessage().getJMSReplyTo();
        } catch (JMSException e) {
            throw new InfrastructureException("Failed to read reply to queue name from message");
        }
    }

    private String extractCorreationId() throws InfrastructureException {
        try {
            return this.getReceivedMessage().getJMSCorrelationID();
        } catch (JMSException e) {
            throw new InfrastructureException("Failed to read correlation id from message");
        }
    }

    private InterconnectObject extractIco() throws InfrastructureException {
        try {
            return InterconnectMapper.fromJson(this.getReceivedMessage().getText(), InterconnectObject.class);
        } catch (final IOException | JMSException e) {
            throw new InfrastructureException("Failed to create ico from message");
        }
    }

    /**
     * @return a response context, created from this context
     * @throws InfrastructureException in case of errors
     */
    public InterconnectContext createResponseContext() throws InfrastructureException {
        return this.receivedContext.createResponseContext(this.responseICO);
    }

    /**
     * @return Request
     */
    public InterconnectContext getReceivedContext() {
        return this.receivedContext;
    }

    /**
     * @return the responseICO
     */
    public InterconnectObject getResponseICO() {
        return this.responseICO;
    }

    /**
     * @param responseICO the responseICO to set
     */
    public void setResponseICO(InterconnectObject responseICO) {
        this.responseICO = responseICO;
    }

    /**
     * @return the createResponseMethod
     */
    public DaemonMethod getCreateResponseMethod() {
        return this.createResponseMethod;
    }

    /**
     * @param createResponseMethod the createResponseMethod to set
     */
    public void setCreateResponseMethod(DaemonMethod createResponseMethod) {
        this.createResponseMethod = createResponseMethod;
    }


    /**
     * @return the lastHandlingRuntime, calculate if {@link #handlingDuration()} has not been called jet.
     */
    private Long getLastHandlingRuntime() {
        if (this.lastHandlingRuntime == null) {
            this.handlingDuration();
        }
        return this.lastHandlingRuntime;
    }

    /**
     * @return a calculated {@link HandlingDuration} containing the runtime up to this point and a corresponding {@link HandlingDurationType}
     */
    public HandlingDuration handlingDuration() {
        final long end = System.currentTimeMillis();
        final long runtime = end - this.executionStartTime;
        this.lastHandlingRuntime = runtime;
        if (runtime > this.createResponseMethod.getTimeoutInMs()) {
            return new HandlingDuration(HandlingDurationType.TIMEOUT, runtime);
        } else if (runtime > (this.createResponseMethod.getTimeoutInMs() / 2L)) {
            return new HandlingDuration(HandlingDurationType.SLOW_RESPONSE, runtime);
        }
        return new HandlingDuration(HandlingDurationType.IN_TIME, runtime);
    }

    /**
     * @return a default timeout message for logging or exceptions
     */
    public String timeoutMessage() {
        return "Response skipped because runtime " + this.getLastHandlingRuntime() + " ms was greater than timeout " +
               this.getCreateResponseMethod().getTimeoutInMs() + " ms for " +
               this.getCreateResponseMethod().getMethod().getName() + "(" +
               this.getReceivedContext().getIcoClass().getSimpleName() + ")" + " with " +
               de.taimos.dvalin.interconnect.model.InterconnectContext.getContext();
    }

    /**
     * @return a default slow response message for logging or exceptions
     */
    public String slowResponseMessage() {
        return "Slow response because runtime " + this.getLastHandlingRuntime() + " ms for " +
               this.getCreateResponseMethod().getMethod().getName() + "(" +
               this.getReceivedContext().getIcoClass().getSimpleName() + ")" + " with " +
               de.taimos.dvalin.interconnect.model.InterconnectContext.getContext();
    }
}
