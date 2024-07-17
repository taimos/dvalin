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


import de.taimos.dvalin.interconnect.core.exceptions.InfrastructureException;

public final class TestHelper {

    /**
     * @param url Broker url
     */
    public static void initBrokerEnv(final String url) {
        System.setProperty(DvalinConnectionFactory.SYSPROP_USERNAME, "admin");
        System.setProperty(DvalinConnectionFactory.SYSPROP_PASSWORD, "admin");
        System.setProperty(DvalinConnectionFactory.SYSPROP_IBROKERURL, url);
        try {
            MessageConnector.start(url);
        } catch (final InfrastructureException e) {
            throw new RuntimeException(e);
        }
    }

    /** */
    public static void closeBrokerEnv() {
        try {
            MessageConnector.stop();
        } catch (final InfrastructureException e) {
            throw new RuntimeException(e);
        }
    }

    private TestHelper() {
        super();
    }

}
