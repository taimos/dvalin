package de.taimos.dvalin.notification.push;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PushMessageWrapper {
    
    private static ObjectMapper MAPPER = new ObjectMapper();
    
    private Map<Platform, PushMessage> payload = new HashMap<>();
    
    public void addPushMessage(PushMessage message) {
        this.payload.put(message.getType(), message);
    }
    
    public String toMessage() throws IOException {
        Map<String, String> snsMessage = new HashMap<>();
        for (Map.Entry<Platform, PushMessage> entry : this.payload.entrySet()) {
            snsMessage.put(entry.getKey().name(), entry.getValue().getPushMessage());
        }
        return MAPPER.writeValueAsString(snsMessage);
    }
}
