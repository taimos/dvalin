package de.taimos.dvalin.interconnect.core;

import de.taimos.dvalin.interconnect.model.service.DaemonError;
import de.taimos.dvalin.jms.exceptions.TimeoutException;

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
     * @throws DaemonError with specific error code
     * @throws TimeoutException in case of communication timeout
     */
    void send(Serializable object, String topicName) throws DaemonError, TimeoutException;

}
