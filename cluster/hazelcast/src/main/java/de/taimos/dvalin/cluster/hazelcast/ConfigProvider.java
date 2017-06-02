package de.taimos.dvalin.cluster.hazelcast;

import com.hazelcast.config.Config;

@FunctionalInterface
public interface ConfigProvider {
    
    /**
     * called by the bean creator to allow custom modifications to the configuration
     *
     * @param config the configuration to modify
     */
    void configure(Config config);
    
}
