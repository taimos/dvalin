package de.taimos.dvalin.interconnect.demo;

/*
 * #%L
 * Dvalin interconnect demo project
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
