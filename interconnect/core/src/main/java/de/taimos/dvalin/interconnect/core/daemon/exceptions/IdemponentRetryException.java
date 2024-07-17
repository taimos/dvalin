package de.taimos.dvalin.interconnect.core.daemon.exceptions;

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

/**
 * Forces a idempotent method call to be retried because of an exception.
 */
public final class IdemponentRetryException extends RuntimeException {

    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = 480732299475266442L;


    /**
     * @param cause Cause
     */
    public IdemponentRetryException(Throwable cause) {
        super(cause);
    }

}
