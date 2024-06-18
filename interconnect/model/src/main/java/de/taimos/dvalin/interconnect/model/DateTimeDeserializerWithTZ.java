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

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

/**
 * Helper class to deserialize maps with DateTime keys
 */
public class DateTimeDeserializerWithTZ extends StdScalarDeserializer<DateTime> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor
	 */
	public DateTimeDeserializerWithTZ() {
		super(DateTime.class);
	}

	@Override
	public DateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
		JsonToken t = jp.getCurrentToken();
		if (t == JsonToken.VALUE_NUMBER_INT) {
			return new DateTime(jp.getLongValue(), DateTimeZone.forTimeZone(ctxt.getTimeZone()));
		}
		if (t == JsonToken.VALUE_STRING) {
			String str = jp.getText().trim();
			if (str.isEmpty()) { // [JACKSON-360]
				return null;
			}
			// catch serialized time zones
			if ((str.charAt(str.length() - 6) == '+') || (str.charAt(str.length() - 1) == 'Z') || (str.charAt(str.length() - 6) == '-')) {
				return new DateTime(str);
			}
			return new DateTime(str, DateTimeZone.forTimeZone(ctxt.getTimeZone()));
		}
		ctxt.handleUnexpectedToken(this.handledType(), jp);
        // never reached
        return null;
	}

	@Override
	public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
		return typeDeserializer.deserializeTypedFromAny(jp, ctxt);
	}
}
