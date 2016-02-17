package de.taimos.dvalin.interconnect.core.spring.test;

/*
 * #%L
 * Dvalin interconnect test library
 * %%
 * Copyright (C) 2016 Taimos GmbH
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
