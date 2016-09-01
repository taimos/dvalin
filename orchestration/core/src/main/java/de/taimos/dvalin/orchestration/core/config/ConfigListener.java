package de.taimos.dvalin.orchestration.core.config;

public interface ConfigListener {
    
    void added(String key, String value);
    
    void changed(String key, String oldValue, String newValue);
    
    void removed(String key, String lastValue);
    
}
