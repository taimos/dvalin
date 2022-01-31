package de.taimos.dvalin.interconnect.core.spring;

import de.taimos.dvalin.interconnect.core.daemon.ADaemonMessageHandler;
import org.slf4j.Logger;

/**
 *  Copyright 2022 Taimos GmbH<br>
 * <br>
 *
 * @author psigloch
 */
public interface IDaemonMessageHandlerFactory {
    /**
     * @param logger the logger to use within the message handler
     * @return the message handler
     */
    ADaemonMessageHandler create(Logger logger);
}
