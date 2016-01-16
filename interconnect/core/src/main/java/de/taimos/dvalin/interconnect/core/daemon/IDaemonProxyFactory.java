package de.taimos.dvalin.interconnect.core.daemon;

import java.util.UUID;

import de.taimos.dvalin.interconnect.model.service.IDaemon;


public interface IDaemonProxyFactory {

    /**
     * @param <D>    Daemon type
     * @param uuid   Universally unique identifier of the request
     * @param daemon Daemon interface
     * @return Proxy
     */
    <D extends IDaemon> D create(UUID uuid, Class<D> daemon);

}
