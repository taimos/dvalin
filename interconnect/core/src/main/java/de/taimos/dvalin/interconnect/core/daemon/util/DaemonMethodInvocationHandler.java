package de.taimos.dvalin.interconnect.core.daemon.util;

import de.taimos.dvalin.interconnect.core.daemon.model.InterconnectContext;
import de.taimos.dvalin.interconnect.core.daemon.model.InterconnectContext.InterconnectContextBuilder;
import de.taimos.dvalin.interconnect.core.daemon.proxy.ADaemonProxyFactory;
import de.taimos.dvalin.interconnect.model.InterconnectObject;
import de.taimos.dvalin.interconnect.model.ivo.daemon.VoidIVO;
import de.taimos.dvalin.interconnect.model.service.Daemon;
import de.taimos.dvalin.interconnect.model.service.DaemonScanner;
import de.taimos.dvalin.interconnect.model.service.DaemonScanner.DaemonMethod;
import de.taimos.dvalin.interconnect.model.service.IDaemon;
import de.taimos.dvalin.jms.exceptions.InfrastructureException;
import de.taimos.dvalin.jms.model.JmsTarget;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.TimeUnit;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
public class DaemonMethodInvocationHandler implements InvocationHandler {

    private final String queueName;
    private final ADaemonProxyFactory aDaemonProxyFactory;

    private DaemonMethodInvocationHandler(ADaemonProxyFactory aDaemonProxyFactory, String queueName) {
        this.queueName = queueName;
        this.aDaemonProxyFactory = aDaemonProxyFactory;
    }

    /**
     * @param daemonInterface     daemon interface class
     * @param aDaemonProxyFactory proxy factory
     * @param <D>                 interface for which the proxy will be created
     * @return a proxy for the {@code daemonInterface}, using the {@code aDaemonProxyFactory}
     */
    @SuppressWarnings("unchecked")
    public static <D extends IDaemon> D createProxy(Class<D> daemonInterface, ADaemonProxyFactory aDaemonProxyFactory) {
        if (!daemonInterface.isAnnotationPresent(Daemon.class)) {
            throw new IllegalArgumentException("Daemon interface has no @Daemon annotation");
        }

        String localQueueName = daemonInterface.getAnnotation(Daemon.class).name() + ".request";

        return (D) Proxy.newProxyInstance(aDaemonProxyFactory.getClass().getClassLoader(),
            new Class<?>[]{daemonInterface}, new DaemonMethodInvocationHandler(aDaemonProxyFactory, localQueueName));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            final DaemonScanner.DaemonMethod dm = DaemonScanner.scan(method);
            if (dm == null) {
                throw new Exception("Method " + method.getName() + " is not a daemon method.");
            }

            InterconnectContext sendContext = this.createSendContext(args, dm);

            if (dm.getType() == DaemonScanner.Type.voit) {
                this.aDaemonProxyFactory.sendRequest(sendContext);
                return null;
            }

            final Class<?> responseClass = DaemonMethodInvocationHandler.getResponseClass(method);
            return this.aDaemonProxyFactory.syncRequest(sendContext, responseClass);
        } catch (Exception e) {
            throw DaemonExceptionMapper.map(e);
        }
    }

    private static Class<?> getResponseClass(Method method) {
        final Class<?> responseClass;
        if (method.getReturnType().equals(Void.TYPE)) {
            responseClass = VoidIVO.class;
        } else {
            responseClass = method.getReturnType();
        }
        return responseClass;
    }

    private InterconnectContext createSendContext(Object[] args, DaemonMethod dm) throws InfrastructureException {
        return new InterconnectContextBuilder() //
            .withUuid(de.taimos.dvalin.interconnect.model.InterconnectContext.getUuid()) //
            .withTarget(JmsTarget.QUEUE) //
            .withDestinationName(this.queueName) //
            .withRequestICO((InterconnectObject) args[0]) //
            .withTimeToLive(dm.getTimeoutInMs(), TimeUnit.MILLISECONDS) //
            .withReceiveTimeout(dm.getTimeoutInMs(), TimeUnit.MILLISECONDS) //
            .withSecure(dm.isSecure()).withIdempotent(dm.isIdempotent()).build();
    }
}
