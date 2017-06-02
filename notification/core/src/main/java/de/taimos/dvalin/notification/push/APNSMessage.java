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

import java.util.HashMap;
import java.util.Map;

public class APNSMessage extends PushMessage {
    
    private final Platform type;
    
    private final String message;
    private String title;
    private int badge;
    
    private final Map<String, Object> customData = new HashMap<>();
    
    public APNSMessage(String message) {
        this(Platform.APNS, message);
    }
    
    public APNSMessage(Platform type, String message) {
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
    public Platform getType() {
        return this.type;
    }
}
