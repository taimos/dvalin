package de.taimos.dvalin.interconnect.core.daemon;

import de.taimos.dvalin.interconnect.model.InterconnectContext;
import de.taimos.dvalin.interconnect.model.InterconnectObject;
import de.taimos.dvalin.interconnect.model.ivo.daemon.VoidIVO;
import de.taimos.dvalin.interconnect.model.service.DaemonError;
import de.taimos.dvalin.interconnect.model.service.DaemonScanner;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
public class DaemonInvocationHandler implements InvocationHandler {

    private final String queueName;
    private final ADaemonProxyFactory aDaemonProxyFactory;

    public DaemonInvocationHandler(ADaemonProxyFactory aDaemonProxyFactory, String queueName) {
        this.queueName = queueName;
        this.aDaemonProxyFactory = aDaemonProxyFactory;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            final DaemonScanner.DaemonMethod dm = DaemonScanner.scan(method);
            if (dm == null) {
                throw new Exception("Method " + method.getName() + " is not a daemon method.");
            }
            if (dm.getType() == DaemonScanner.Type.voit) {
                this.aDaemonProxyFactory.sendToQueue(InterconnectContext.getUuid(), this.queueName,
                    (InterconnectObject) args[0], dm.isSecure());
                return null;
            }
            final Class<?> responseClass;
            if (method.getReturnType().equals(Void.TYPE)) {
                responseClass = VoidIVO.class;
            } else {
                responseClass = method.getReturnType();
            }
            return this.aDaemonProxyFactory.syncRequest(InterconnectContext.getUuid(), this.queueName,
                (InterconnectObject) args[0], responseClass, dm.getTimeoutInMs(), TimeUnit.MILLISECONDS, dm.isSecure());
        } catch (final ExecutionException e) {
            if (e.getCause() instanceof DaemonError) {
                throw e.getCause();
            }
            if (e.getCause() instanceof Exception) {
                throw e.getCause();
            }
            throw e;
        }
    }
}
