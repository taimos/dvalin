package de.taimos.dvalin.interconnect.model.service;

import de.taimos.dvalin.interconnect.model.event.IEvent;

/**
 * @param <T> the type of the event the handler is subscribed to
 */
public interface IEventHandler<T extends IEvent> {

    /**
     * @return the class of the event the handler is subscribed to
     */
    Class<T> getEventType();

    /**
     * @param event the event to handle
     */
    void handleEvent(T event);
}
