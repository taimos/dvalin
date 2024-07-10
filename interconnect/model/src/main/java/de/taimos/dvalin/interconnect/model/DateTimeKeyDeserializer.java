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

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdKeyDeserializer;

/**
 * Helper class to deserialize maps with DateTime keys
 */
public class DateTimeKeyDeserializer extends StdKeyDeserializer {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor
	 */
	public DateTimeKeyDeserializer() {
		super(-1, DateTime.class);
	}

	@Override
	protected Object _parse(String key, DeserializationContext ctxt) throws Exception {
		if (key.isEmpty()) { // [JACKSON-360]
			return null;
		}
		return new DateTime(key, DateTimeZone.forTimeZone(ctxt.getTimeZone()));
	}

}
