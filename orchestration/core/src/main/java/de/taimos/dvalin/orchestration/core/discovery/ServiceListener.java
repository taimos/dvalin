package de.taimos.dvalin.orchestration.core.discovery;

public interface ServiceListener {
    
    void instanceRegistered(ServiceInstance instance);
    
    void instanceChanged(ServiceInstance instance);
    
    void instanceUnregistered(ServiceInstance instance);
    
}
