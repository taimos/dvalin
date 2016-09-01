package de.taimos.dvalin.orchestration.core.config;

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
