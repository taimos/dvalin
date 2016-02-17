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

public class CryptoException extends Exception {

	private static final long serialVersionUID = 1L;


	/**
	 * @param message the exception message
	 */
	public CryptoException(String message) {
		super(message);
	}

    /**
     * @param message the exception message
     * @param cause the root cause
     */
    public CryptoException(String message, Throwable cause) {
        super(message, cause);
    }

}
