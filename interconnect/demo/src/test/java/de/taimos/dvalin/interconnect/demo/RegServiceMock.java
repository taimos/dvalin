package de.taimos.dvalin.interconnect.demo;

import de.taimos.dvalin.interconnect.core.spring.test.APrototypeHandlerMock;
import de.taimos.dvalin.interconnect.core.spring.test.PrototypeHandlerMock;
import de.taimos.dvalin.interconnect.demo.api.IRegistrationService;
import de.taimos.dvalin.interconnect.demo.model.RegUserIVO_v1;
import de.taimos.dvalin.interconnect.model.InterconnectContext;
import de.taimos.dvalin.interconnect.model.service.DaemonError;

@PrototypeHandlerMock
public class RegServiceMock extends APrototypeHandlerMock implements IRegistrationService {

    @Override
    public void registerUser(RegUserIVO_v1 ivo) throws DaemonError {
        System.out.println();
        System.out.println("Request UUID: " + InterconnectContext.getUuid());
        System.out.println("Creation data: " + ivo.getUserId() + " - " + ivo.getName());
    }
}
