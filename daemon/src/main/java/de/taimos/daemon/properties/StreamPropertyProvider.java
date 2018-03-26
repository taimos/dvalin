package de.taimos.daemon.properties;

/*
 * #%L
 * Daemon Library
 * %%
 * Copyright (C) 2012 - 2016 Taimos GmbH
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

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class StreamPropertyProvider implements IPropertyProvider {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	@Override
	public Map<String, String> loadProperties() {
		final HashMap<String, String> map = new HashMap<>();
		try (InputStream s = this.getStream()) {
			final Properties prop = new Properties();
			prop.load(s);
			for (final Entry<Object, Object> entry : prop.entrySet()) {
				map.put(entry.getKey().toString(), entry.getValue().toString());
			}
		} catch (Exception e) {
			this.logger.error("Failed to load properties from stream", e);
		}
		return map;
	}
	
	protected abstract InputStream getStream() throws Exception;
	
}
