package de.taimos.dvalin.interconnect.model;

/*
 * #%L
 * Dvalin interconnect transfer data model
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

/**
 * Interconnect constants.
 */
public final class InterconnectConstants {

    /** maximum time to wait when sending a message over the message queue (in milliseconds) */
    public static final long IVO_SENDTIMEOUT = 10000;

	/** maximum time to wait when receiving a message over the message queue (in milliseconds) */
    public static final long IVO_RECEIVETIMEOUT = 10000;

	/** the message priority to use when sending a message over the message queue */
    public static final int IVO_MSGPRIORITY = 5;

	/** name of the boolean message property that indicates whether the message contains a regular result or an error object */
    public static final String MSGPROP_ERROR = "error";


}
