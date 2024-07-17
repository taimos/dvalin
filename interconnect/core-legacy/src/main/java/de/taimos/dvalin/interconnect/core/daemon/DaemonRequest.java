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

import javax.jms.Destination;

import de.taimos.dvalin.interconnect.model.InterconnectObject;


public final class DaemonRequest {

    private final String correlationID;

    private final Destination replyTo;

    private final InterconnectObject ico;


    /**
     * @param aCorrelationID Correlation ID
     * @param aReplyTo       Reply to
     * @param anIco          IVO
     */
    public DaemonRequest(final String aCorrelationID, final Destination aReplyTo, final InterconnectObject anIco) {
        super();
        this.correlationID = aCorrelationID;
        this.replyTo = aReplyTo;
        this.ico = anIco;
    }

    /**
     * @return Correlation ID
     */
    public String getCorrelationID() {
        return this.correlationID;
    }

    /**
     * @return Reply to
     */
    public Destination getReplyTo() {
        return this.replyTo;
    }

    /**
     * @return IVO
     */
    public InterconnectObject getInterconnectObject() {
        return this.ico;
    }

}
