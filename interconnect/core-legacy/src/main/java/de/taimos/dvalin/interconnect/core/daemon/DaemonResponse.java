package de.taimos.dvalin.interconnect.core.daemon;

/*
 * #%L
 * Dvalin interconnect core library
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


import de.taimos.dvalin.interconnect.model.InterconnectObject;

public final class DaemonResponse {

    private final DaemonRequest request;

    private final InterconnectObject response;


    /**
     * @param aRequest  Request
     * @param aResponse Response
     */
    public DaemonResponse(final DaemonRequest aRequest, final InterconnectObject aResponse) {
        super();
        this.request = aRequest;
        this.response = aResponse;
    }

    /**
     * @return Request
     */
    public DaemonRequest getRequest() {
        return this.request;
    }

    /**
     * @return Response
     */
    public InterconnectObject getResponse() {
        return this.response;
    }

}
