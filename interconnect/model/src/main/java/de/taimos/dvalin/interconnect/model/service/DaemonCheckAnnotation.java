package de.taimos.dvalin.interconnect.model.service;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public final class DaemonCheckAnnotation {
	
	public static void testDaemonInterface(Class<? extends  IDaemon> daemonInterface) {
		if (!daemonInterface.isAnnotationPresent(Daemon.class)) {
			throw new IllegalArgumentException("No @Daemon found in " + daemonInterface);
		}
		boolean atLeatsOneDaemonMethod = false;
		final Set<DaemonScanner.DaemonMethod> res = new HashSet<>();
		for (final Method method : daemonInterface.getMethods()) {
			try {
				final DaemonScanner.DaemonMethod dm = DaemonScanner.scan(method);
				if (dm == null) {
                    throw new IllegalArgumentException("Invalid method " + method.getName() + " found in IDaemon interface " + daemonInterface.getName());
				} else {
					if (res.contains(dm)) {
                        throw new IllegalArgumentException("IVO " + dm.getRequest().getSimpleName() + " is used more than once as request  in IDaemon interface " + daemonInterface.getName());
					}
					res.add(dm);
					atLeatsOneDaemonMethod = true;
				}
			} catch (final IllegalStateException e) {
                throw new IllegalArgumentException("Invalid method " + method.getName() + " found in IDaemon interface " + daemonInterface.getName() + ": " + e.getMessage());
			}
		}
		if (!atLeatsOneDaemonMethod) {
            throw new IllegalArgumentException("No @DaemonRequestMethod or @DaemonReceiverMthod found in " + daemonInterface);
		}
		
	}
}
