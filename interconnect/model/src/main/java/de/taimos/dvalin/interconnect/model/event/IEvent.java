package de.taimos.dvalin.interconnect.model.event;

import de.taimos.dvalin.interconnect.model.InterconnectObject;
import org.joda.time.DateTime;

import java.util.UUID;

/**
 * Interconnect event object marker interface. Common base class for all interconnect event objects.
 */
public interface IEvent extends InterconnectObject {

    /**
     * @return the autmoaticaly genereated id of the event
     */
    UUID getEventId();

    /**
     * @return the creationdate of the event
     */
    DateTime getCreationDate();

    /**
     * @return a clone
     */
    @Override
    IEvent clone();

    /**
     * @param <T> builder type
     * @return the builder initialized with this
     */
    <T extends IEventBuilder> T createBuilder();
}
