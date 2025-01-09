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

import de.taimos.dvalin.interconnect.model.service.DaemonError;
import de.taimos.dvalin.jms.IJmsConnector;
import de.taimos.dvalin.interconnect.core.exceptions.TimeoutException;

import java.io.Serializable;

/**
 * Abstract IVO refresh sender.
 *
 * @author psigloch, fzwirn
 */
@SuppressWarnings("unused")
public class IVORefreshSender extends AToTopicSender {


    /**
     * @param jmsConnector that will create the JMS connections
     */
    public IVORefreshSender(IJmsConnector jmsConnector) {
        super(jmsConnector);
    }

    /**
     * @param object the object
     * @throws DaemonError      with specific error code
     * @throws TimeoutException in case of communication timeout
     */
    public void send(Serializable object) throws DaemonError, TimeoutException {
        this.send(object, System.getProperty(IJmsConnector.SYSPROP_UPDATE_TOPIC));
    }
}
