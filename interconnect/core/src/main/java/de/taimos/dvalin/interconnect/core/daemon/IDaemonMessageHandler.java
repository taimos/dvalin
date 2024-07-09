package de.taimos.dvalin.interconnect.core.daemon;

import javax.jms.Message;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
public interface IDaemonMessageHandler {

    /**
     * @param message Message
     * @throws Exception If no registered method was found for the incomming InterconnectObject or Insecure call or no (valid) Request UUID
     *                   or no
     */

    void onMessage(Message message) throws Exception;
}
