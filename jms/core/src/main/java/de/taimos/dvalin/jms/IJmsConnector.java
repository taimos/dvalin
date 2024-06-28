package de.taimos.dvalin.jms;

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

import de.taimos.dvalin.jms.exceptions.InfrastructureException;
import de.taimos.dvalin.jms.exceptions.MessageCryptoException;
import de.taimos.dvalin.jms.model.DvalinJmsReceiveObject;
import de.taimos.dvalin.jms.model.DvalinJmsResponseObject;
import de.taimos.dvalin.jms.model.DvalinJmsSendObject;
import de.taimos.dvalin.jms.model.JmsTarget;

import javax.jms.Destination;

/**
 * Connector to connect to JMS providers.
 *
 * @author Thorsten Hoeger
 * @author fzwirn
 */
public interface IJmsConnector {
    /**
     * name of the system property that contains the interconnect update topic name
     */
    String SYSPROP_UPDATE_TOPIC = "interconnect.jms.updatetopic";

    /**
     * name of the system property that contains the interconnect virtual topic prefix
     */
    String SYSPROP_VIRTUAL_TOPIC_PREFIX = "interconnect.jms.virtualtopic.prefix";

    /**
     * the default request timeout
     */
    long REQUEST_TIMEOUT = 10000;

    /**
     * the message priority to use when sending a message over the message queue
     */
    int MSGPRIORITY = 5;

    void send(DvalinJmsSendObject object) throws MessageCryptoException, InfrastructureException;

    DvalinJmsResponseObject request(DvalinJmsSendObject object) throws MessageCryptoException, InfrastructureException;

    DvalinJmsResponseObject receive(DvalinJmsReceiveObject object) throws InfrastructureException, MessageCryptoException;

    /**
     * @param type of the destination
     * @param name of the destination
     * @return a JMS destination
     */
    Destination createDestination(JmsTarget type, String name);

}
