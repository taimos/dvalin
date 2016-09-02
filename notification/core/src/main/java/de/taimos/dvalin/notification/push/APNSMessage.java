package de.taimos.dvalin.notification.push;

import java.util.HashMap;
import java.util.Map;

public class APNSMessage extends PushMessage {
    
    public static final String TYPE_APNS = "APNS";
    public static final String TYPE_APNS_SANDBOX = "APNS_SANDBOX";
    
    private final String type;
    
    private final String message;
    private String title;
    private int badge;
    
    private final Map<String, Object> customData = new HashMap<>();
    
    public APNSMessage(String message) {
        this(TYPE_APNS, message);
    }
    
    public APNSMessage(String type, String message) {
        this.type = type;
        this.message = message;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setBadge(int badge) {
        this.badge = badge;
    }
    
    public void addCustomData(String key, Object value) {
        this.customData.put(key, value);
    }
    
    @Override
    protected Map<String, Object> getPayload() {
        Map<String, Object> map = new HashMap<>();
        map.putAll(this.customData);
        
        if (this.title != null) {
            Map<String, Object> alert = new HashMap<>();
            alert.put("body", this.message);
            alert.put("title", this.title);
            map.put("alert", alert);
        } else {
            map.put("alert", this.message);
        }
        map.put("badge", this.badge);
        return map;
    }
    
    @Override
    public String getType() {
        return this.type;
    }
}
