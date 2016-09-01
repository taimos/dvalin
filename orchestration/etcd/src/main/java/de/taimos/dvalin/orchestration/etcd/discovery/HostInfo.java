package de.taimos.dvalin.orchestration.etcd.discovery;

import java.util.Map;

public class HostInfo {
    
    private String host;
    private String status;
    private Map<String, Object> properties;
    
    public String getHost() {
        return this.host;
    }
    
    public void setHost(String host) {
        this.host = host;
    }
    
    public String getStatus() {
        return this.status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Map<String, Object> getProperties() {
        return this.properties;
    }
    
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
