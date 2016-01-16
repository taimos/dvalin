package de.taimos.dvalin.interconnect.core.spring.test;

import org.springframework.beans.factory.annotation.Autowired;

import de.taimos.dvalin.interconnect.core.daemon.IDaemonProxyFactory;
import de.taimos.dvalin.interconnect.core.spring.message.IMessageMock;
import de.taimos.dvalin.interconnect.model.ivo.daemon.PingIVO;
import de.taimos.dvalin.interconnect.model.ivo.daemon.PongIVO;
import de.taimos.dvalin.interconnect.model.service.DaemonError;
import de.taimos.dvalin.interconnect.model.service.IDaemon;
import de.taimos.dvalin.interconnect.model.service.IDaemonHandler;

public class APrototypeHandlerMock implements IDaemonHandler {

    private final IContext ctx;

    @Autowired
    private IDaemonProxyFactory factory;

    @Autowired(required = false)
    protected IMessageMock messageMock;


    /**
     * @param ctx the request context
     */
    public APrototypeHandlerMock(IContext ctx) {
        super();
        this.ctx = ctx;
    }

    @Override
    public IContext getContext() {
        return this.ctx;
    }

    protected <I extends IDaemon> I createProxy(Class<I> clazz) {
        return this.factory.create(this.getContext().uuid(), clazz);
    }

    @Override
    public void afterRequestHook() {
        // nothing to do here
    }

    @Override
    public void beforeRequestHook() {
        // nothing to do here
    }

    @Override
    public void exceptionHook(final RuntimeException exception) throws DaemonError {
        // nothing to do here
    }

    @Override
    public PongIVO alive(PingIVO arg0) {
        return new PongIVO.PongIVOBuilder().build();
    }
}
