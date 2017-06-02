package de.taimos.dvalin.daemon;

/*-
 * #%L
 * Daemon support for dvalin
 * %%
 * Copyright (C) 2015 - 2017 Taimos GmbH
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

import java.util.Map;
import java.util.stream.Collectors;

import de.taimos.daemon.properties.IPropertyProvider;

public class EnvPropertyProvider implements IPropertyProvider {
	
	private static final String ENV_PREFIX = "DVALIN_";
	
	@Override
	public Map<String, String> loadProperties() {
	    return System.getenv().entrySet()
            .stream()
            .filter(entry -> entry.getKey().startsWith(ENV_PREFIX))
            .collect(Collectors.toMap(e -> this.generateKey(e.getKey()), Map.Entry::getValue));
	}
	
	private String generateKey(String key) {
		return key.substring(ENV_PREFIX.length()).replace("_", ".").toLowerCase();
	}
    
    /**
     * @return true if any environment variable for Dvalin is present
     */
    public static boolean isConfigured() {
	    return System.getenv().keySet().stream().anyMatch(s -> s.startsWith(ENV_PREFIX));
	}
	
}
