package de.taimos.dvalin.orchestration.core.discovery;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * provide service discovery using the orchestration tooling
 */
public interface ServiceDiscovery {
    
    // methods concering the current service instance
    
    /**
     * register the current instance in the registry
     */
    void registerInstance();
    
    /**
     * update the current instance in the registry
     */
    void updateInstance();
    
    /**
     * remove the current instance from the registry
     */
    void unregisterInstance();
    
    /**
     * set additional properties for the current instance
     *
     * @param properties the properties to attach to the instance
     */
    void setAdditionalProperties(Map<String, Object> properties);
    
    /**
     * @return the current properties of the instance
     */
    Optional<Map<String, Object>> getAdditionalProperties();
    
    // methods to fetch information about other instances
    
    /**
     * fetch a list of all service instances for a given service
     *
     * @param serviceName the name of the service to find
     * @return list of registered instances for the given service
     */
    List<ServiceInstance> getInstancesForService(String serviceName);
    
    /**
     * fetch the additional properties of the given instance if available
     *
     * @param instance the service instance
     * @return the additional properties
     */
    Optional<Map<String, Object>> getAdditionalProperties(ServiceInstance instance);
    
    /**
     * register a listener for changes to service instance
     *
     * @param serviceName the service to register the listener for
     * @param listener    the listener to register
     */
    void addListenerForService(String serviceName, ServiceListener listener);
    
    /**
     * remove the listener
     *
     * @param serviceName the service to remove the listener from
     * @param listener    the listener to remove
     */
    void removeListenerForService(String serviceName, ServiceListener listener);
    
}
