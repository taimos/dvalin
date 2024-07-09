package de.taimos.dvalin.jms.exceptions;

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
 * An exception for JMS message base crypto errors
 *
 * @author fzwirn
 */
public class MessageCryptoException extends SerializationException {


    private static final long serialVersionUID = -6378917176126978412L;

    /**
     * default constructor
     */
    public MessageCryptoException() {
        this("Message security check failed");
    }

    /**
     * @param cause of the exception
     */
    public MessageCryptoException(Throwable cause) {
        this("Message security check failed", cause);
    }

    /**
     * @param message the exception message
     */
    public MessageCryptoException(String message) {
        super(message);
    }

    /**
     * @param message the exception message
     * @param cause   the root cause
     */
    public MessageCryptoException(String message, Throwable cause) {
        super(message, cause);
    }

}
