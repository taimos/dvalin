package de.taimos.dvalin.jms.test;

import de.taimos.dvalin.jms.IJmsConnector;
import de.taimos.dvalin.jms.model.JmsContext;
import de.taimos.dvalin.jms.model.JmsResponseContext;

import javax.annotation.Nonnull;
import javax.jms.Message;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
@SuppressWarnings("unused")
public class JmsConnectorMock implements IJmsConnector {

    private final List<JmsContext> receivedContext = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void send(@Nonnull JmsContext context) {
        this.receivedContext.add(context);
    }

    @Override
    public JmsResponseContext<? extends Message> request(@Nonnull JmsContext context) {
        this.receivedContext.add(context);
        return null;
    }

    @Override
    public JmsResponseContext<? extends Message> receive(@Nonnull JmsContext context) {
        this.receivedContext.add(context);
        return null;
    }

    @Override
    public List<Message> receiveBulkFromDestination(JmsContext context, int maxSize) {
        this.receivedContext.add(context);
        return Collections.emptyList();
    }

    /**
     * @return the receivedContext
     */
    public List<JmsContext> getReceivedContext() {
        synchronized (this.receivedContext) {
            return this.receivedContext;
        }
    }

    /**
     * clear the received context
     */
    public void clearReceivedContext() {
        synchronized (this.receivedContext) {
            this.receivedContext.clear();
        }
    }
}
