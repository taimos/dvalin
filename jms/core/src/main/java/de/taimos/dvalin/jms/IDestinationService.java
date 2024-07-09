package de.taimos.dvalin.jms;

import de.taimos.dvalin.jms.model.JmsTarget;

import javax.annotation.Nonnull;
import javax.jms.Destination;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
public interface IDestinationService {

    /**
     * @param type of the destination
     * @param name of the destination
     * @return a JMS destination
     * @throws UnsupportedOperationException in case the target can not be mapped by the implementation
     */
    Destination createDestination(@Nonnull JmsTarget type, @Nonnull String name) throws UnsupportedOperationException;
}
