package de.taimos.dvalin.interconnect.model;

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
		if (key.length() == 0) { // [JACKSON-360]
			return null;
		}
		return new DateTime(key, DateTimeZone.forTimeZone(ctxt.getTimeZone()));
	}

}
