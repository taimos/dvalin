package de.taimos.dvalin.interconnect.core.spring.requestresponse;

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

import java.util.UUID;

import de.taimos.dvalin.interconnect.core.daemon.IDaemonRequestResponse;
import de.taimos.dvalin.interconnect.model.InterconnectObject;
import de.taimos.dvalin.interconnect.model.service.DaemonError;


/**
 * Must be implemented for mocking interconnect communication via {@link IDaemonRequestResponse}.
 */
public interface IRequestMock {

    /**
     * @param <R>           Response type
     * @param uuid          Universally unique identifier of the request
     * @param queue         Queue name
     * @param request       Request
     * @param responseClazz Response class
     * @return Response
     * @throws DaemonError If something went wrong
     */
    <R> R in(UUID uuid, String queue, InterconnectObject request, Class<R> responseClazz) throws DaemonError;

    /**
     * @param uuid    Universally unique identifier of the request
     * @param queue   Queue name
     * @param request Request
     */
    void in(UUID uuid, String queue, InterconnectObject request);
}
