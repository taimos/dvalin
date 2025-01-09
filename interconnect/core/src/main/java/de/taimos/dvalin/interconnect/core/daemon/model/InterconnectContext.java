package de.taimos.dvalin.interconnect.core.daemon.model;

import com.google.common.base.Preconditions;
import de.taimos.dvalin.interconnect.model.InterconnectMapper;
import de.taimos.dvalin.interconnect.model.InterconnectObject;
import de.taimos.dvalin.interconnect.core.exceptions.InfrastructureException;
import de.taimos.dvalin.jms.model.JmsContext;
import de.taimos.dvalin.jms.model.JmsTarget;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.UUID;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
public class InterconnectContext extends JmsContext {

    /**
     * name of the message header to specify interconnect request UUID
     */
    public static final String HEADER_REQUEST_UUID = "InterconnectRequestUUID";

    /**
     * name of the message header to specify interconnect ICO class
     */
    public static final String HEADER_ICO_CLASS = "InterconnectICOClass";

    public static class InterconnectContextBuilder extends AJmsContextBuilder<InterconnectContextBuilder> {

        private UUID uuid = UUID.randomUUID();
        private InterconnectObject requestICO;
        private boolean idempotent = false;

        /**
         * @param original context to create a new builder from
         */
        public InterconnectContextBuilder(JmsContext original) {
            super(original);
        }

        /**
         * Default builder constructor
         */
        public InterconnectContextBuilder() {
        }

        /**
         * @param uuid for the context
         * @return builder
         */
        public InterconnectContextBuilder withUuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }


        /**
         * @param idempotent is the context idempotent
         * @return builder
         */
        public InterconnectContextBuilder withIdempotent(boolean idempotent) {
            this.idempotent = idempotent;
            return this;
        }

        /**
         * @param requestICO for the context, will override the body with the content of the requestIco
         * @return builder
         * @throws InfrastructureException in case of errors
         */
        public InterconnectContextBuilder withRequestICO(InterconnectObject requestICO) throws InfrastructureException {
            this.requestICO = requestICO;
            this.body = InterconnectContext.createBody(this.requestICO);
            return this;
        }

        @Override
        protected void validate() {
            super.validate();
            Preconditions.checkNotNull(this.uuid, "Universally unique identifier of the request");
        }

        @Override
        @Nonnull
        public InterconnectContext build() {
            if ("true".equalsIgnoreCase(System.getProperty("jms.no-retry"))) {
                this.idempotent = false;
            }
            if (this.uuid != null) {
                this.headers.put(InterconnectContext.HEADER_REQUEST_UUID, this.uuid.toString());
            }
            if (this.requestICO != null) {
                this.headers.put(InterconnectContext.HEADER_ICO_CLASS, this.requestICO.getClass().getName());
            }
            this.validate();
            return new InterconnectContext(this);
        }
    }


    private final InterconnectObject requestIco;
    private final boolean idempotent;

    protected InterconnectContext(InterconnectContextBuilder builder) {
        super(builder);
        this.requestIco = builder.requestICO;
        this.idempotent = builder.idempotent;
    }

    /**
     * @return the requestIco
     */
    public InterconnectObject getRequestIco() {
        return this.requestIco;
    }

    /**
     * @return the icoClass
     */
    public Class<? extends InterconnectObject> getIcoClass() {
        return this.requestIco.getClass();
    }

    /**
     * @return the idempotent
     */
    public boolean isIdempotent() {
        return this.idempotent;
    }

    private static String createBody(InterconnectObject ico) throws InfrastructureException {
        try {
            return InterconnectMapper.toJson(ico);
        } catch (final IOException e) {
            throw new InfrastructureException("Failed to read message");
        }
    }

    /**
     * @param responseICO ico to use for the response
     * @return a new response context with the responseIco, created from this context
     * @throws InfrastructureException in case of errors
     */
    public InterconnectContext createResponseContext(InterconnectObject responseICO) throws InfrastructureException {
        if (this.getReplyToDestination() != null) {
            return new InterconnectContextBuilder(this)//
                .withDestination(this.getReplyToDestination()) //
                .withRequestICO(responseICO) //
                .withIdempotent(false) //
                .withTarget(JmsTarget.DESTINATION).build();
        }
        return new InterconnectContextBuilder(this)//
            .withDestinationName(this.getReplyToQueueName()) //
            .withRequestICO(responseICO) //
            .withIdempotent(false) //
            .withTarget(JmsTarget.QUEUE).build();
    }
}
