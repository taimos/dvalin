package de.taimos.dvalin.orchestration.core.config;

public interface GlobalConfiguration {
    
    void setConfiguration(String key, String value);
    
    void setConfiguration(String key, String value, Integer ttlSeconds);
    
    void removeConfiguration(String key);
    
    String getConfiguration(String key);
    
    void addConfigurationListener(ConfigListener listener);
    
    void removeConfigurationListener(ConfigListener listener);
    
}
