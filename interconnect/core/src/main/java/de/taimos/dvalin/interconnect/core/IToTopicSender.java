package de.taimos.dvalin.interconnect.core;

import java.io.Serializable;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
public interface IToTopicSender {
    /**
     * @param object    the object
     * @param topicName name of the topic you want to use
     */
    void send(Serializable object, String topicName);

}
