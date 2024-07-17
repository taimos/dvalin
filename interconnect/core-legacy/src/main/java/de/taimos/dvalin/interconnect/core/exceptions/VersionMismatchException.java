package de.taimos.dvalin.interconnect.core.exceptions;

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

public class VersionMismatchException extends InfrastructureException {

    private static final long serialVersionUID = 1L;


    /**
     * @param expected the expected version
     * @param actual   the actual version received
     */
    public VersionMismatchException(String expected, String actual) {
        super(String.format("Interconnect version mismatch! Expected version %s and received version %s", expected, actual));
    }

}
