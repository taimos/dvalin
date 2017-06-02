package de.taimos.dvalin.notification.push;

/*-
 * #%L
 * Dvalin notification service
 * %%
 * Copyright (C) 2016 - 2017 Taimos GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
