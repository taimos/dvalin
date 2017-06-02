package de.taimos.dvalin.interconnect.model.service;

/*-
 * #%L
 * Dvalin interconnect transfer data model
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
