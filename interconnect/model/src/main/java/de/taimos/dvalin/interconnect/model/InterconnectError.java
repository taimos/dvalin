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

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;


/**
 * Error object for passing over the Interconnect.
 */
public class InterconnectError implements InterconnectObject {

	/** version UID for serialization */
	private static final long serialVersionUID = 1L;


	/**
	 * A list of possible error type. Extend as needed.
	 */
	public enum InterconnectErrorType {
		/** a problem occurred during serialization */
		SerializationFail,
		/** a problem occurred during deserialization */
		DeserializationFail,
		/** the requested IVOs cannot be retrieved */
		CannotRetrieveIVOs
	}


	/** the type of error */
	private final InterconnectErrorType type;

	/** a description of the problem */
	private final String message;


	/**
	 * Sole constructor.
	 *
	 * @param type the type of the error
	 * @param message a description of the problem
	 */
	@JsonCreator
	public InterconnectError(@JsonProperty("type") InterconnectErrorType type, @JsonProperty("message") String message) {
		this.type = type;
		this.message = message;
	}

	/**
	 * Returns the type of error.
	 *
	 * @return the type of error
	 */
	public InterconnectErrorType getType() {
		return this.type;
	}

	/**
	 * Returns a description of the problem.
	 *
	 * @return a description of the problem
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * A JSON representation of this object.
	 *
	 * @return a JSON representation of this object
	 * @throws JsonGenerationException if the JSON string cannot be generated
	 * @throws JsonMappingException if the object cannot be mapped to a JSON string
	 * @throws IOException if an I/O related problem occurred
	 */
	public String toJson() throws IOException {
		return InterconnectMapper.toJson(this);
	}

	/**
	 * Returns the InterconnectError object stored in the given JSON string.
	 *
	 * @param json the JSON data
	 * @return the InterconnectError object stored in the given JSON string.
	 * @throws JsonParseException if the JSON string cannot be parsed
	 * @throws JsonMappingException if the given JSON data cannot be mapped to an InterconnectError object
	 * @throws IOException if an I/O related problem occurred
	 */
	public static InterconnectError fromJson(String json) throws IOException {
		return InterconnectMapper.fromJson(json, InterconnectError.class);
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// cannot happen
            throw new RuntimeException("Cloning of InterconnectError failed", e);
		}
	}

}
