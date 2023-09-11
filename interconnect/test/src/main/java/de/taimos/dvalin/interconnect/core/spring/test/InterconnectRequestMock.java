package de.taimos.dvalin.interconnect.core.spring.test;

/*
 * #%L
 * Dvalin interconnect test library
 * %%
 * Copyright (C) 2016 Taimos GmbH
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

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;

import de.taimos.daemon.spring.annotations.TestComponent;
import de.taimos.dvalin.interconnect.core.spring.requestresponse.IRequestMock;
import de.taimos.dvalin.interconnect.model.InterconnectContext;
import de.taimos.dvalin.interconnect.model.InterconnectList;
import de.taimos.dvalin.interconnect.model.InterconnectObject;
import de.taimos.dvalin.interconnect.model.ivo.IVO;
import de.taimos.dvalin.interconnect.model.service.Daemon;
import de.taimos.dvalin.interconnect.model.service.DaemonError;
import de.taimos.dvalin.interconnect.model.service.DaemonScanner;
import de.taimos.dvalin.interconnect.model.service.DaemonScanner.DaemonMethod;
import de.taimos.dvalin.interconnect.model.service.IDaemonHandler;

@TestComponent("requestMock")
public class InterconnectRequestMock implements IRequestMock {

    private static final Logger LOGGER = LoggerFactory.getLogger(InterconnectRequestMock.class);

    private final ConcurrentHashMap<String, RequestMockHandler> handlers = new ConcurrentHashMap<>();

    @Autowired
    private ListableBeanFactory beanFactory;


    /**
     *
     */
    @PostConstruct
    public void init() {
        final String[] prototypeNames = this.beanFactory.getBeanNamesForType(APrototypeHandlerMock.class);
        for (final String name : prototypeNames) {
            Class<? extends APrototypeHandlerMock> type = (Class<? extends APrototypeHandlerMock>) this.beanFactory.getType(name);
            InterconnectRequestMock.LOGGER.info("Found request mock of type {} with name {}", type, name);
            this.registerPrototype(type, name);
        }
        final String[] singletonNames = this.beanFactory.getBeanNamesForType(ASingletonHandlerMock.class);
        for (final String name : singletonNames) {
            Class<? extends ASingletonHandlerMock> type = (Class<? extends ASingletonHandlerMock>) this.beanFactory.getType(name);
            InterconnectRequestMock.LOGGER.info("Found request mock of type {} with name {}", type, name);
            this.registerSingleton(type, name);
        }
    }

    private static String getQueueName(final Class<?>[] interfaces) {
        for (final Class<?> iface : interfaces) {
            if (iface.isAnnotationPresent(Daemon.class)) {
                return iface.getAnnotation(Daemon.class).name() + ".request";
            }
        }
        throw new RuntimeException("No @Daemon annotation found");
    }

    private void registerPrototype(final Class<? extends APrototypeHandlerMock> handler, final String beanName) {
        final Class<?>[] interfaces = handler.getInterfaces();
        final String queueName = InterconnectRequestMock.getQueueName(interfaces);
        this.handlers.put(queueName, new RMHWrapper(handler, beanName, this.beanFactory, false));
    }

    private void registerSingleton(final Class<? extends ASingletonHandlerMock> handler, final String beanName) {
        final Class<?>[] interfaces = handler.getInterfaces();
        final String queueName = InterconnectRequestMock.getQueueName(interfaces);
        this.handlers.put(queueName, new RMHWrapper(handler, beanName, this.beanFactory, true));
    }

    /**
     * @param queue the queue name
     * @return <code>true</code> if a handler is registered
     */
    public boolean hasHandler(String queue) {
        return this.handlers.containsKey(queue);
    }

    @Override
    public <R> R in(UUID uuid, String queue, InterconnectObject request, Class<R> responseClazz) throws DaemonError {
        return this.request(uuid, queue, request, responseClazz);
    }

    /**
     * @param <R>           Response type
     * @param uuid          Universally unique identifier of the request
     * @param queue         Queue name
     * @param request       Request
     * @param responseClazz Response class
     * @return Response
     * @throws DaemonError If something went wrong
     */
    public <R> R request(UUID uuid, String queue, InterconnectObject request, Class<R> responseClazz) throws DaemonError {
        RequestMockHandler handler = this.handlers.get(queue);
        if (handler != null) {
            return handler.handle(uuid, request, responseClazz);
        }
        throw new RuntimeException("No handler for queue: " + queue + " and request: " + request);
    }

    @Override
    public void in(UUID uuid, String queue, InterconnectObject request) {
        this.receive(uuid, queue, request);
    }

    /**
     * @param uuid    Universally unique identifier of the request
     * @param queue   Queue name
     * @param request Request
     */
    public void receive(UUID uuid, String queue, InterconnectObject request) {
        RequestMockHandler handler = this.handlers.get(queue);
        if (handler == null) {
            throw new RuntimeException("No handler for queue: " + queue + " and request: " + request);
        }
        handler.handle(uuid, request);
    }


    public interface RequestMockHandler {

        /**
         * @param <R>
         * @param uuid
         * @param ico
         * @param responseClazz
         * @return the response
         * @throws DaemonError
         */
        <R> R handle(UUID uuid, InterconnectObject ico, Class<R> responseClazz) throws DaemonError;

        /**
         * @param uuid
         * @param ico
         */
        void handle(UUID uuid, InterconnectObject ico);

    }

    public static class RMHWrapper implements RequestMockHandler {

        private final Map<Class<? extends InterconnectObject>, DaemonMethod> methods = Maps.newHashMap();
        private final BeanFactory beanFactory;
        private final String beanName;
        private final boolean singleton;


        /**
         * @param handler     the handler class
         * @param beanName    the bean name
         * @param beanFactory the bean factory
         * @param singleton   Singleton mock?
         */
        public RMHWrapper(Class<? extends IDaemonHandler> handler, String beanName, BeanFactory beanFactory, final boolean singleton) {
            this.beanName = beanName;
            this.beanFactory = beanFactory;
            Set<DaemonMethod> scan = DaemonScanner.scan(handler);
            for (DaemonMethod dm : scan) {
                this.methods.put(dm.getRequest(), dm);
            }
            this.singleton = singleton;
        }

        @Override
        @SuppressWarnings("rawtypes")
        public <R> R handle(UUID uuid, InterconnectObject ico, Class<R> responseClazz) throws DaemonError {
            DaemonMethod daemonMethod = this.methods.get(ico.getClass());
            if (daemonMethod != null) {
                try {
                    final IDaemonHandler mockInstance = this.createMockInstance(ico.getClass(), uuid);
                    InterconnectObject ivo = daemonMethod.invoke(mockInstance, ico);
                    if (responseClazz.isArray() && (ivo instanceof InterconnectList)) {
                        final InterconnectList list = (InterconnectList) ivo;
                        final Object obj = Array.newInstance(responseClazz.getComponentType(), list.getElements().size());
                        return (R) list.getElements().toArray(DaemonScanner.object2Array(responseClazz.getComponentType(), obj));
                    } else if ((ivo instanceof InterconnectList) && List.class.isAssignableFrom(responseClazz)) {
                        final InterconnectList list = (InterconnectList) ivo;
                        return (R) list.getElements();
                    } else if (responseClazz.isAssignableFrom(ivo.getClass())) {
                        return responseClazz.cast(ivo);
                    }
                } catch (IllegalAccessException | IllegalArgumentException | SecurityException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    if (e.getTargetException() instanceof DaemonError) {
                        throw (DaemonError) e.getTargetException();
                    }
                    throw new RuntimeException(e);
                }
            }
            throw new RuntimeException();
        }

        @Override
        public void handle(UUID uuid, InterconnectObject ico) {
            DaemonMethod daemonMethod = this.methods.get(ico.getClass());
            if (daemonMethod != null) {
                try {
                    final IDaemonHandler mockInstance = this.createMockInstance(ico.getClass(), uuid);
                    if (daemonMethod.invoke(mockInstance, ico) == null) {
                        return;
                    }
                } catch (IllegalAccessException | IllegalArgumentException | SecurityException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
            throw new RuntimeException();
        }

        @SuppressWarnings("unchecked")
        private IDaemonHandler createMockInstance(Class<? extends InterconnectObject> ivoClass, UUID uuid) {
            InterconnectContext.reset();
            InterconnectContext.setUuid(uuid);
            if (IVO.class.isAssignableFrom(ivoClass)) {
                InterconnectContext.setRequestClass((Class<? extends IVO>) ivoClass);
            } else {
                InterconnectContext.setRequestClass(IVO.class);
            }
            final IDaemonHandler mockInstance;
            if (this.singleton) {
                mockInstance =  (ASingletonHandlerMock) this.beanFactory.getBean(this.beanName);
            } else {
                // TODO check Spring startup state to prevent errors from prototype mocks
                //				if (SpringMain.isStarting()) {
                //					throw new RuntimeException("Protoype mocks can not be used during starting phase!");
                //				}
                mockInstance = (APrototypeHandlerMock) this.beanFactory.getBean(this.beanName);
            }
            return mockInstance;
        }

    }
}
