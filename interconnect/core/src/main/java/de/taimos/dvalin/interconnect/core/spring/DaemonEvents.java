package de.taimos.dvalin.interconnect.core.spring;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.stereotype.Component;

import de.taimos.dvalin.interconnect.core.InterconnectConnector;
import de.taimos.dvalin.interconnect.core.MessageConnector;
import de.taimos.dvalin.interconnect.model.InterconnectMapper;
import de.taimos.dvalin.interconnect.model.InterconnectObject;
import de.taimos.dvalin.interconnect.model.ivo.IVO;

@Component
public final class DaemonEvents implements IDaemonEvents, MessageListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @SuppressWarnings("rawtypes")
    private final ConcurrentHashMap<Class<? extends IVO>, CopyOnWriteArraySet<IDaemonEventListener>> listeners = new ConcurrentHashMap<>();

    private final Executor executor = Executors.newCachedThreadPool();

    @Autowired
    private IDaemonMessageSender messageSender;

    private DefaultMessageListenerContainer container;

    @Autowired(required = false)
    private ConnectionFactory jmsFactory;


    /** */
    public DaemonEvents() {
        super();
    }

    /** */
    public void start() {
        this.container = new DefaultMessageListenerContainer();
        this.container.setPubSubDomain(true);
        this.container.setConnectionFactory(this.jmsFactory);
        this.container.setDestinationName("events.global");
        this.container.setMessageListener(this);
        this.container.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
        this.container.setConcurrentConsumers(1);
        this.container.initialize();
        this.container.start();
    }

    /** */
    public void stop() {
        this.container.stop();
    }

    @SuppressWarnings("rawtypes")
    void emit(final IVO event) {
        if (this.listeners.containsKey(event.getClass())) {
            final CopyOnWriteArraySet<IDaemonEventListener> l = this.listeners.get(event.getClass());
            if (l.size() != 0) {
                this.logger.info("Event " + event.getClass().getSimpleName());
            }
            for (final IDaemonEventListener listener : l) {
                this.executor.execute(new Runnable() {

                    @SuppressWarnings("unchecked")
                    @Override
                    public void run() {
                        listener.onEvent(event);
                    }
                });
            }
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    public <I extends IVO> void listen(final Class<I> eventIVOClazz, IDaemonEventListener<I> listener) {
        this.listeners.putIfAbsent(eventIVOClazz, new CopyOnWriteArraySet<IDaemonEventListener>());
        this.listeners.get(eventIVOClazz).add(listener);
    }

    @Override
    public <I extends IVO> void unlisten(Class<I> eventIVOClazz, IDaemonEventListener<I> listener) {
        if (this.listeners.containsKey(eventIVOClazz)) {
            this.listeners.get(eventIVOClazz).remove(listener);
        }
    }


    private final ConcurrentHashMap<String, DateTime> eventErrors = new ConcurrentHashMap<>();


    private static final String icoClassToSaveString(final String icoClass) {
        if (icoClass == null) {
            return "nullclass";
        }
        return "class:" + icoClass;
    }

    void logEventError(final String icoClass, final String message, final Throwable throwable) {
        final String saveIcoClass = DaemonEvents.icoClassToSaveString(icoClass);
        final DateTime lastSeen = this.eventErrors.get(saveIcoClass);
        final DateTime now = new DateTime();
        final DateTime treshold = now.minusMinutes(30);
        if ((lastSeen == null) || lastSeen.isBefore(treshold)) {
            if (lastSeen == null) {
                this.eventErrors.putIfAbsent(saveIcoClass, now);
            } else {
                this.eventErrors.replace(saveIcoClass, lastSeen, now);
            }
            this.logger.error(message, throwable);
        }
    }

    @Override
    public void onMessage(final Message message) {
        final String icoClass;
        try {
            icoClass = message.getStringProperty(InterconnectConnector.HEADER_ICO_CLASS); // can be null
        } catch (final Exception e) {
            this.logger.error("Exception", e);
            return;
        }
        try {
            if (message instanceof TextMessage) {
                final TextMessage textMessage = (TextMessage) message;
                this.logger.debug("TextMessage received: {}", textMessage.getText());
                if (MessageConnector.isMessageSecure(textMessage)) {
                    MessageConnector.decryptMessage(textMessage);
                }
                final InterconnectObject ico;
                try {
                    ico = InterconnectMapper.fromJson(textMessage.getText());
                } catch (final Exception e) {
                    this.logEventError(icoClass, "Event not supported", e);
                    return;
                }
                if (ico instanceof IVO) {
                    this.emit((IVO) ico);
                } else {
                    this.logEventError(icoClass, "Event not an IVO", null);
                }
            } else {
                this.logEventError(icoClass, "Event not a TextMessage", null);
            }
        } catch (final Exception e) {
            // we are in non transactional wonderland so we catch the exception which leads to a lost event.
            this.logEventError(icoClass, "Exception", e);
        }
    }

    @Override
    public void publish(final IVO event) {
        this.logger.debug("Publish " + event.getClass().getSimpleName());
        try {
            this.messageSender.sendToTopic("events.global", event, false);
        } catch (final Exception e) {
            this.logger.error("Can not publish event", e);
        }
    }


}
