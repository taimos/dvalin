package de.taimos.dvalin.interconnect.core.daemon;

import org.slf4j.Logger;

/**
 * Copyright 2022 Taimos GmbH<br>
 * <br>
 *
 * @author psigloch
 */
public interface IDaemonMessageHandlerFactory {

    /**
     * @param logger the logger to use within the message handler
     * @return the message handler
     */
    IDaemonMessageHandler create(Logger logger);

}
