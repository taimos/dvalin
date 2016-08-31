package de.taimos.dvalin.orchestration.core.discovery;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.taimos.daemon.LifecyclePhase;

public interface ServiceDiscovery {
    
    // methods concering the current service instance
    
    void registerInstance(LifecyclePhase phase);
    
    void updateInstance(LifecyclePhase phase);
    
    void unregisterInstance();
    
    void setAdditionalProperties(Map<String, Object> properties);
    
    // methods to fetch information about other instances
    
    List<ServiceInstance> getInstancesForService(String serviceName);
    
    Optional<Map<String, Object>> getAdditionalProperties(ServiceInstance instance);
    
    void addListenerForService(String serviceName, ServiceListener listener);
    
    void removeListenerForService(String serviceName, ServiceListener listener);
    
    void addListenerForServiceInstance(String serviceName, String instanceId, ServiceListener listener);
    
    void removeListenerForServiceInstance(String serviceName, String instanceId, ServiceListener listener);
    
}
