package de.taimos.dvalin.interconnect.model.event;

/*-
 * #%L
 * Dvalin interconnect transfer data model
 * %%
 * Copyright (C) 2016 - 2017 Taimos GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
