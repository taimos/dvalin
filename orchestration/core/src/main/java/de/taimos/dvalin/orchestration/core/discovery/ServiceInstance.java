package de.taimos.dvalin.orchestration.core.discovery;

import de.taimos.daemon.LifecyclePhase;

public class ServiceInstance {
    
    private final String host;
    private final String serviceName;
    private final String instanceId;
    private final LifecyclePhase phase;
    
    public ServiceInstance(String host, String serviceName, String instanceId, LifecyclePhase phase) {
        this.host = host;
        this.serviceName = serviceName;
        this.instanceId = instanceId;
        this.phase = phase;
    }
    
    public String getHost() {
        return this.host;
    }
    
    public String getServiceName() {
        return this.serviceName;
    }
    
    public String getInstanceId() {
        return this.instanceId;
    }
    
    public LifecyclePhase getPhase() {
        return this.phase;
    }
    
    public ServiceInstance withPhase(LifecyclePhase newPhase) {
        return new ServiceInstance(this.host, this.serviceName, this.instanceId, newPhase);
    }
}
