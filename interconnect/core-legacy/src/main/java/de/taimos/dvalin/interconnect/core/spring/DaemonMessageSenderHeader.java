package de.taimos.dvalin.interconnect.core.spring;

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

import java.util.UUID;

import com.google.common.base.Preconditions;

import de.taimos.dvalin.interconnect.core.InterconnectConnector;
import de.taimos.dvalin.interconnect.model.InterconnectObject;


public final class DaemonMessageSenderHeader {

    public enum Field {

        /**
         * Universally unique identifier of the request.
         */
        RequestUUID(InterconnectConnector.HEADER_REQUEST_UUID),

        /**
         * Interconnect Object class.
         */
        icoClass(InterconnectConnector.HEADER_ICO_CLASS);

        private final String name;


        Field() {
            this.name = this.name();
        }

        Field(final String aName) {
            this.name = aName;
        }

        /**
         * @return Name
         */
        public String getName() {
            return this.name;
        }
    }


    private final Field field;

    private final Object value;


    /**
     * The constructor is hidden because we have factory methods guaranteeing the correct type of value.
     *
     * @param field Field
     * @param value Value
     */
    private DaemonMessageSenderHeader(final Field field, final Object value) {
        super();
        this.field = field;
        this.value = value;
    }

    /**
     * @param requestUUID Universally unique identifier of the request
     * @return Header for Universally unique identifier of the request
     */
    public static DaemonMessageSenderHeader createRequestUUID(final UUID requestUUID) {
        Preconditions.checkNotNull(requestUUID, "Universally unique identifier of the request");
        return new DaemonMessageSenderHeader(Field.RequestUUID, requestUUID.toString());
    }

    /**
     * @param ico Interconnect Object
     * @return Header for Interconnect Object class
     */
    public static DaemonMessageSenderHeader createICOClass(final Class<? extends InterconnectObject> ico) {
        Preconditions.checkNotNull(ico, "Interconnect Object");
        return new DaemonMessageSenderHeader(Field.icoClass, ico.getName());
    }

    /**
     * @return Key
     */
    public Field getField() {
        return this.field;
    }

    /**
     * @return Value
     */
    public Object getValue() {
        return this.value;
    }

}
