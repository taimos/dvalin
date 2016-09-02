package de.taimos.dvalin.notification.push;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PushMessageWrapper {
    
    private static ObjectMapper MAPPER = new ObjectMapper();
    
    private Map<String, PushMessage> payload = new HashMap<>();
    
    public void addPushMessage(PushMessage message) {
        this.payload.put(message.getType(), message);
    }
    
    public String toMessage() throws IOException {
        Map<String, String> snsMessage = new HashMap<>();
        for (Map.Entry<String, PushMessage> entry : this.payload.entrySet()) {
            snsMessage.put(entry.getKey(), entry.getValue().getPushMessage());
        }
        return MAPPER.writeValueAsString(snsMessage);
    }
}
