/**
 *
 */
package de.taimos.dvalin.interconnect.core;

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

import java.io.Serializable;

public class IVORefreshSender extends ToTopicSender {

    private static IVORefreshSender instance = new IVORefreshSender();

    private IVORefreshSender() {
        super();
    }

    /**
     * @return the singleton
     */
    public static IVORefreshSender getInstance() {
        return IVORefreshSender.instance;
    }

    /**
     * @param object the object
     */
    public void send(Serializable object) {
        this.send(object, System.getProperty(MessageConnector.SYSPROP_UPDATE_TOPIC));
    }
}
