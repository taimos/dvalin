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

import de.taimos.dvalin.interconnect.model.event.EventDomain;
import de.taimos.dvalin.interconnect.model.event.IEvent;
import de.taimos.dvalin.interconnect.model.service.DaemonError;
import de.taimos.dvalin.jms.IJmsConnector;
import de.taimos.dvalin.interconnect.core.exceptions.TimeoutException;
import org.springframework.core.annotation.AnnotationUtils;

import java.io.Serializable;

/**
 * Event sender
 *
 * @author psigloch, fzwirn
 */
@SuppressWarnings("unused")
public class EventSender extends AToTopicSender {

    private static final String PROP_DEFAULT_VIRTUAL_TOPIC_PREFIX = "VirtualTopic";


    private final String virtualTopicPrefix;

    /**
     * @param jmsConnector that will create the JMS connections
     */
    public EventSender(IJmsConnector jmsConnector) {
        super(jmsConnector);
        this.virtualTopicPrefix = System.getProperty(IJmsConnector.SYSPROP_VIRTUAL_TOPIC_PREFIX,
            EventSender.PROP_DEFAULT_VIRTUAL_TOPIC_PREFIX);
    }


    @Override
    public void send(Serializable object, String topicName) throws DaemonError, TimeoutException {
        if (object instanceof IEvent) {
            this.send((IEvent) object);
        } else {
            super.send(object, topicName);
        }
    }

    /**
     * @param object the object
     * @throws DaemonError      with specific error code
     * @throws TimeoutException in case of communication timeout
     */
    public void send(IEvent object) throws DaemonError, TimeoutException {
        EventDomain domainAnnotation = AnnotationUtils.findAnnotation(object.getClass(), EventDomain.class);
        if (domainAnnotation == null) {
            this.logger.error("The event {} has no domain annotation", object.getClass().getSimpleName());
            return;
        }
        if (domainAnnotation.value().isEmpty()) {
            this.logger.error("The domainname for the event {} is empty", object.getClass().getSimpleName());
            return;
        }
        super.send(object, this.virtualTopicPrefix + "." + domainAnnotation.value());
    }
}
