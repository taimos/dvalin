package de.taimos.dvalin.interconnect.core.event;

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
import de.taimos.daemon.spring.annotations.ProdComponent;
import de.taimos.dvalin.interconnect.core.MessageConnector;
import de.taimos.dvalin.interconnect.model.InterconnectMapper;
import de.taimos.dvalin.interconnect.model.event.EventDomain;
import de.taimos.dvalin.interconnect.model.event.IEvent;
import de.taimos.dvalin.interconnect.model.service.IEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ErrorHandler;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.PostConstruct;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ProdComponent("eventMessageListener")
public class EventMessageListener implements MessageListener, ErrorHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Collection<IEventHandler<IEvent>> eventHandlers;

    private MultiValueMap<Class<IEvent>, IEventHandler<IEvent>> eventHandlerMap;

    /** */
    public EventMessageListener() {
        super();
    }

    /** */
    @PostConstruct
    public void start() {
        this.eventHandlerMap = new LinkedMultiValueMap<>();
        for(IEventHandler<IEvent> eventHandler : this.eventHandlers) {
            if(eventHandler == null || eventHandler.getEventType() == null) {
                continue;
            }
            this.eventHandlerMap.add(eventHandler.getEventType(), eventHandler);
        }
    }

    /**
     * @return a list of domains to subsscribe to
     */
    public Collection<String> getDomains() {
        Set<String> result = new HashSet<>();
        for(IEventHandler<IEvent> eventHandler : this.eventHandlers) {
            if(eventHandler == null || eventHandler.getEventType() == null) {
                continue;
            }
            Annotation domainAnnotation = eventHandler.getEventType().getAnnotation(EventDomain.class);
            if(domainAnnotation != null && !((EventDomain) domainAnnotation).name().isEmpty()) {
                result.add(((EventDomain) domainAnnotation).name());
            }
        }
        return result;
    }

    @SuppressWarnings({"SuspiciousMethodCalls", "unchecked"})
    @Override
    public void onMessage(Message message) {
        try {
            if(message instanceof TextMessage) {
                final TextMessage textMessage = (TextMessage) message;
                this.logger.debug("TextMessage received: {}", textMessage.getText());
                final boolean secure = MessageConnector.isMessageSecure(textMessage);
                if(secure) {
                    MessageConnector.decryptMessage(textMessage);
                }
                final IEvent eventIn = InterconnectMapper.fromJson(textMessage.getText(), IEvent.class);
                List<IEventHandler<IEvent>> eventHandlers = this.eventHandlerMap.get(eventIn.getClass());
                if(eventHandlers != null) {
                    for(IEventHandler<IEvent> eventHandler : eventHandlers) {
                        eventHandler.handleEvent(eventIn);
                    }
                }
            }
        } catch(final Exception e) {
            // we are in non transactional wonderland so we catch the exception which leads to a request without a response.
            this.logger.error("Exception", e);
        }
    }

    @Override
    public void handleError(Throwable throwable) {
        this.logger.warn("An error during event handling occured", throwable);
    }
}
