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
import de.taimos.dvalin.interconnect.model.InterconnectObject;
import de.taimos.dvalin.interconnect.model.event.EventDomain;
import de.taimos.dvalin.interconnect.model.event.IEvent;
import de.taimos.dvalin.interconnect.model.service.IEventHandler;
import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;
import org.springframework.util.ErrorHandler;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@ProdComponent("eventMessageListener")
public class EventMessageListener implements MessageListener, ErrorHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${interconnect.jms.consumers:2-8}")
    private String consumers;

    @Value("${serviceName}")
    private String serviceName;

    @Value("${interconnect.jms.virtualtopic.prefix:VirtualTopic}")
    private String virtualTopicPrefix;
    @Value("${interconnect.jms.virtualtopic.consumerprefix:Consumer}")
    private String consumerPrefix;

    @Autowired
    @Qualifier("DvalinConnectionFactory")
    private ConnectionFactory jmsFactory;
    @Autowired
    private ApplicationContext applicationContext;

    private Set<IEventHandler> eventHandlers;
    private Set<MessageListenerContainer> listeners;

    /** */
    public EventMessageListener() {
        super();
    }

    /** */
    @PostConstruct
    public void initEventListeners() {
        this.listeners = new HashSet<>();
        this.eventHandlers = new HashSet<>();
        for(Object o : this.applicationContext.getBeansWithAnnotation(EventHandler.class).values()) {
            if(o instanceof IEventHandler) {
                this.eventHandlers.add((IEventHandler) o);
            }
        }

        for(String domain : this.getDomains()) {
            this.logger.info("Registered EventListener for topic {}", domain);
            DefaultMessageListenerContainer dmlc = this.createQueueListener(domain);
            this.listeners.add(dmlc);
        }
    }


    /** */
    @PreDestroy
    public void stopEventListeners() {
        for(MessageListenerContainer listener : this.listeners) {
            listener.stop();
        }
    }

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
                final InterconnectObject eventIn = InterconnectMapper.fromJson(textMessage.getText(), InterconnectObject.class);
                for(IEventHandler eventHandler : this.eventHandlers) {
                    if(eventHandler != null && eventIn.getClass().isAssignableFrom(eventHandler.getEventType())) {
                        eventHandler.handleEvent((IEvent) eventIn);
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

    private DefaultMessageListenerContainer createQueueListener(String domain) {
        ActiveMQQueue virtualTopic = new ActiveMQQueue(this.consumerPrefix + "." + this.serviceName + "." + this.virtualTopicPrefix + "." + domain);
        DefaultMessageListenerContainer dmlc = new DefaultMessageListenerContainer();
        dmlc.setConnectionFactory(this.jmsFactory);
        dmlc.setErrorHandler(this);
        dmlc.setConcurrency(this.consumers);
        dmlc.setDestination(virtualTopic);
        dmlc.setMessageListener(this);
        dmlc.afterPropertiesSet();
        dmlc.start();
        return dmlc;
    }

    private Collection<String> getDomains() {
        Set<String> result = new HashSet<>();
        if(this.eventHandlers == null || this.eventHandlers.isEmpty()) {
            return result;
        }
        for(IEventHandler eventHandler : this.eventHandlers) {
            if(eventHandler == null || eventHandler.getEventType() == null) {
                continue;
            }
            Annotation domainAnnotation = AnnotationUtils.findAnnotation(eventHandler.getEventType(), EventDomain.class);
            if(domainAnnotation != null && !((EventDomain) domainAnnotation).value().isEmpty()) {
                result.add(((EventDomain) domainAnnotation).value());
            }
        }
        return result;
    }

}
