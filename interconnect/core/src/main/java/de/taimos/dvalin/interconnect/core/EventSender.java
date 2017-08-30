package de.taimos.dvalin.interconnect.core;

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
