package de.taimos.dvalin.orchestration.core.discovery;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ServiceDiscovery {
    
    // methods concering the current service instance
    
    void registerInstance();
    
    void updateInstance();
    
    void unregisterInstance();
    
    void setAdditionalProperties(Map<String, Object> properties);
    
    Optional<Map<String, Object>> getAdditionalProperties();
    
    // methods to fetch information about other instances
    
    List<ServiceInstance> getInstancesForService(String serviceName);
    
    Optional<Map<String, Object>> getAdditionalProperties(ServiceInstance instance);
    
    void addListenerForService(String serviceName, ServiceListener listener);
    
    void removeListenerForService(String serviceName, ServiceListener listener);
    
}
