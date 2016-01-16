package de.taimos.dvalin.interconnect.core.spring.test;


import de.taimos.dvalin.interconnect.model.ivo.daemon.PingIVO;
import de.taimos.dvalin.interconnect.model.ivo.daemon.PongIVO;
import de.taimos.dvalin.interconnect.model.service.DaemonError;
import de.taimos.dvalin.interconnect.model.service.IDaemonHandler;

public class ASingletonHandlerMock implements IDaemonHandler {

    private final ThreadLocal<IContext> contextLocal = new ThreadLocal<>();


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
    public IContext getContext() {
        final IContext c = this.contextLocal.get();
        if (c == null) {
            throw new RuntimeException("No context was set");
        }
        return c;
    }

    /**
     * @param c Context
     */
    public void setContext(final IContext c) {
        this.contextLocal.set(c);
    }

    @Override
    public PongIVO alive(PingIVO arg0) {
        return new PongIVO.PongIVOBuilder().build();
    }

}
