package de.taimos.dvalin.interconnect.core.spring.test;


import de.taimos.dvalin.interconnect.model.InterconnectContext;
import de.taimos.dvalin.interconnect.model.ivo.daemon.PingIVO;
import de.taimos.dvalin.interconnect.model.ivo.daemon.PongIVO;
import de.taimos.dvalin.interconnect.model.service.DaemonError;
import de.taimos.dvalin.interconnect.model.service.IDaemonHandler;

public class ASingletonHandlerMock implements IDaemonHandler {

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
    @Deprecated
    public IContext getContext() {
        return InterconnectContext.getContext();
    }

    @Override
    public PongIVO alive(PingIVO arg0) {
        return new PongIVO.PongIVOBuilder().build();
    }

}
