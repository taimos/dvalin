package de.taimos.dvalin.interconnect.core.daemon;

import de.taimos.dvalin.interconnect.model.service.IDaemon;


public interface IDaemonProxyFactory {

    /**
     * @param <D>    Daemon type
     * @param daemon Daemon interface
     * @return Proxy
     */
    <D extends IDaemon> D create(Class<D> daemon);

}
