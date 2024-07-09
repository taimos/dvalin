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
import de.taimos.dvalin.jms.exceptions.SerializationException;
import de.taimos.dvalin.jms.model.JmsContext;
import de.taimos.dvalin.jms.model.JmsResponseContext;

import javax.annotation.Nonnull;
import javax.jms.Message;
import java.util.List;

/**
 * Connector to connect to JMS providers.
 *
 * @author Thorsten Hoeger
 * @author fzwirn
 */
@SuppressWarnings("unused")
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
     * the default request timeout (in Milliseconds)
     */
    long REQUEST_TIMEOUT = 10000;

    /**
     * the message priority to use when sending a message over the message queue
     */
    int MSGPRIORITY = 5;


    /**
     * @param context holds all necessary information for the send operation
     * @throws InfrastructureException in case of general errors
     * @throws SerializationException  in case of problems with encryption
     */
    void send(@Nonnull JmsContext context) throws InfrastructureException, SerializationException;

    /**
     * @param context holds all necessary information for the request operation
     * @return response of the request
     * @throws InfrastructureException in case of general errors
     * @throws SerializationException  in case of problems with encryption
     */
    JmsResponseContext<? extends Message> request(@Nonnull JmsContext context) throws InfrastructureException, SerializationException;

    /**
     * @param context holds all necessary information for the reception operation
     * @return response received
     * @throws InfrastructureException in case of general errors
     * @throws SerializationException  in case of problems with encryption
     */
    JmsResponseContext<? extends Message> receive(@Nonnull JmsContext context) throws InfrastructureException, SerializationException;

    /**
     * @param context holds all necessary information for the request operation
     * @param maxSize maximum number of messages to wait for.
     * @return List of response {@link Message}
     * @throws InfrastructureException in case of general errors
     * @throws SerializationException  in case of problems with encryption
     */
    List<Message> receiveBulkFromDestination(JmsContext context, int maxSize) throws InfrastructureException, SerializationException;

}
