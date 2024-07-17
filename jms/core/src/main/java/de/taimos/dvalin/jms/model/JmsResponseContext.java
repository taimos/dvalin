package de.taimos.dvalin.jms.model;


import javax.annotation.Nonnull;
import javax.jms.Message;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
public class JmsResponseContext<T extends Message> {

    private final T receivedTextMessage;

    /**
     * @param receivedTextMessage of the response
     */
    public JmsResponseContext(@Nonnull T receivedTextMessage) {
        this.receivedTextMessage = receivedTextMessage;
    }

    /**
     * @return the textMessage
     */
    @Nonnull
    public T getReceivedMessage() {
        return this.receivedTextMessage;
    }
}
