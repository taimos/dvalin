package de.taimos.dvalin.interconnect.core.daemon.util;

import de.taimos.dvalin.interconnect.model.InterconnectObject;
import de.taimos.dvalin.interconnect.model.service.DaemonScanner;
import de.taimos.dvalin.interconnect.model.service.DaemonScanner.DaemonMethod;
import de.taimos.dvalin.interconnect.model.service.IDaemonHandler;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright 2022 Taimos GmbH<br>
 * <br>
 *
 * @author psigloch
 */
public class DaemonMethodRegistry {

    private final Map<Class<? extends InterconnectObject>, RegistryEntry> registry;

    /**
     * @param aHandlerClazzes all handler classes
     */
    public DaemonMethodRegistry(final Collection<Class<? extends IDaemonHandler>> aHandlerClazzes) {
        final Map<Class<? extends InterconnectObject>, RegistryEntry> reg = new HashMap<>();
        for (Class<? extends IDaemonHandler> aHandlerClazz : aHandlerClazzes) {
            for (final DaemonScanner.DaemonMethod re : DaemonScanner.scan(aHandlerClazz)) {
                reg.put(re.getRequest(), new RegistryEntry(aHandlerClazz, re));
            }
        }
        this.registry = Collections.unmodifiableMap(reg);
    }

    /**
     * @param icoClass the interconnect object class
     * @return the registry entry
     */
    public RegistryEntry get(Class<? extends InterconnectObject> icoClass) {
        return this.registry.get(icoClass);
    }

    /**
     * @param icoClass the interconnect object class
     * @return the daemon method
     */
    public DaemonMethod getMethod(Class<? extends InterconnectObject> icoClass) {
        RegistryEntry registryEntry = this.registry.get(icoClass);
        if (registryEntry == null) {
            return null;
        }
        return registryEntry.getMethod();
    }

    public static class RegistryEntry {
        private final Class<? extends IDaemonHandler> aHandlerClazz;
        private final DaemonMethod method;

        RegistryEntry(Class<? extends IDaemonHandler> aHandlerClazz, DaemonMethod method) {
            this.aHandlerClazz = aHandlerClazz;
            this.method = method;
        }

        /**
         * @return the aHandlerClazz
         */
        public Class<? extends IDaemonHandler> getHandlerClazz() {
            return this.aHandlerClazz;
        }

        /**
         * @return the method
         */
        public DaemonMethod getMethod() {
            return this.method;
        }
    }
}
