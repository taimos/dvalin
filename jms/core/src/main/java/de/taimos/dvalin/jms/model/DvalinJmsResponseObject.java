package de.taimos.dvalin.jms.model;


import javax.jms.TextMessage;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
public class DvalinJmsResponseObject {

    private final TextMessage textMessage;

    public DvalinJmsResponseObject(TextMessage textMessage) {
        this.textMessage = textMessage;
    }

    /**
     * @return the textMessage
     */
    public TextMessage getTextMessage() {
        return this.textMessage;
    }
}
