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

import de.taimos.dvalin.interconnect.model.InterconnectMapper;
import de.taimos.dvalin.interconnect.model.event.EventDomain;
import de.taimos.dvalin.interconnect.model.event.IEvent;
import org.springframework.core.annotation.AnnotationUtils;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Annotation;

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


    public void send(Serializable object, String topicName) {
        if(object instanceof IEvent) {
            this.send((IEvent) object);
        } else {
            super.send(object, topicName);
        }
    }

    /**
     * @param object the object
     */
    public void send(IEvent object) {
        Annotation domainAnnotation = AnnotationUtils.findAnnotation(object.getClass(), EventDomain.class);
        if(domainAnnotation == null) {
            this.logger.error("The event {} has no domain annotation", object.getClass().getSimpleName());
            return;
        }
        if(((EventDomain) domainAnnotation).value().isEmpty()) {
            this.logger.error("The domainname for the event {} is empty", object.getClass().getSimpleName());
            return;
        }
        super.send(object, this.virtualTopicPrefix + "." + ((EventDomain) domainAnnotation).value());
    }

    protected Message getMessage(Serializable object, Session session) throws JMSException, IOException {
        if(object instanceof IEvent) {
            String json = InterconnectMapper.toJson((IEvent) object);
            return session.createTextMessage(json);
        }
        return session.createObjectMessage(object);
    }
}
