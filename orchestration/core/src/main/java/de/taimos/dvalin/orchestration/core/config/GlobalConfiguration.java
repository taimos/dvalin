package de.taimos.dvalin.orchestration.core.config;

/*-
 * #%L
 * Dvalin service orchestration
 * %%
 * Copyright (C) 2016 - 2017 Taimos GmbH
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
 * provide a global and dynamic configuration using the orchestration tooling
 */
public interface GlobalConfiguration {
    
    /**
     * set global configuration value
     *
     * @param key   the key to use
     * @param value the value to set
     */
    void setConfiguration(String key, String value);
    
    /**
     * set global configuration value with automatic expiry
     *
     * @param key        the key to use
     * @param value      the value to set
     * @param ttlSeconds number of seconds to keep configuration alive
     */
    void setConfiguration(String key, String value, Integer ttlSeconds);
    
    /**
     * remove a global configuration key
     *
     * @param key the key to remove
     */
    void removeConfiguration(String key);
    
    /**
     * fetch a global configuration value
     *
     * @param key the key to fetch
     * @return the value of the given global configuration
     */
    String getConfiguration(String key);
    
    /**
     * register a listener for configuration changes
     *
     * @param listener the listener to register
     */
    void addConfigurationListener(ConfigListener listener);
    
    /**
     * remove the given listener
     *
     * @param listener the listener to remove
     */
    void removeConfigurationListener(ConfigListener listener);
    
}
