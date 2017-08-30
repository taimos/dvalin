package de.taimos.dvalin.interconnect.core;

/*-
 * #%L
 * Dvalin interconnect core library
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
import java.io.Serializable;

public class EventSender extends ToTopicSender {

    private static final String PROP_DEFAULT_VIRTUAL_TOPIC_PREFIX = "VirtualTopic";

    private static EventSender instance = new EventSender();

    private String virtualTopicPrefix;

    private EventSender() {
        super();
        this.virtualTopicPrefix = System.getProperty(MessageConnector.SYSPROP_VIRTUAL_TOPIC_PREFIX, EventSender.PROP_DEFAULT_VIRTUAL_TOPIC_PREFIX);
    }

    /**
     * @return the singleton
     */
    public static EventSender getInstance() {
        return EventSender.instance;
    }


    /**
     * @param object    the object
     * @param topicName name of the topic you want to use
     */
    @Override
    public void send(Serializable object, String topicName) {
        super.send(object, this.virtualTopicPrefix + "." + topicName);
    }
}
