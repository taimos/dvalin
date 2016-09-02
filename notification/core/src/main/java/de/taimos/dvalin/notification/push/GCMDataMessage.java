package de.taimos.dvalin.notification.push;

import java.util.HashMap;
import java.util.Map;

public class GCMDataMessage extends PushMessage {
    
    private final Map<String, String> data = new HashMap<>();
    
    public GCMDataMessage(String message) {
        this.data.put("message", message);
    }
    
    public void addCustomData(String key, String value) {
        this.data.put(key, value);
    }
    
    @Override
    protected Map<String, Object> getPayload() {
        Map<String, Object> map = new HashMap<>();
        map.put("data", this.data);
        return map;
    }
    
    @Override
    public String getType() {
        return "GCM";
    }
}
