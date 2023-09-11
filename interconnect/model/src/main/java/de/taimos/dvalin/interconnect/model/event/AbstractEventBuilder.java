package de.taimos.dvalin.interconnect.model.event;

import org.joda.time.DateTime;

import jakarta.annotation.Nonnull;
import java.util.UUID;

/**
 * Convenience base class for EventBuilders
 */
public abstract class AbstractEventBuilder<E extends AbstractEventBuilder<?>> implements IEventBuilder {

    private UUID eventId = UUID.randomUUID();
    private DateTime creationDate = DateTime.now();

    /**
     * This field is required.
     * logisticContainer
     *
     * @param eventId the value to set
     * @return the builder
     **/
    @Nonnull
    public E withEventId(UUID eventId) {
        this.eventId = eventId;
        return (E) this;
    }

    /**
     * @return the event Id
     */
    public UUID getEventId() {
        return this.eventId;
    }


    /**
     * @return the creation date
     */
    public DateTime getCreationDate() {
        return this.creationDate;
    }

    /**
     * This field is required.
     * logisticContainer
     *
     * @param creationDate the value to set
     * @return the builder
     **/
    @Nonnull
    public E withCreationDate(DateTime creationDate) {
        this.creationDate = creationDate;
        return (E) this;
    }

}
