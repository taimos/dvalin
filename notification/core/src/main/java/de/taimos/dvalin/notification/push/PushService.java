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

public interface PushService {
    
    /**
     * register a device with the notification service. If the token is already registered, the existing internal id is returned
     *
     * @param platform    the platform of the device
     * @param deviceToken the device token
     * @param userData    the custom data to save with the device
     * @return the internal id of the registration
     */
    String registerDevice(Platform platform, String deviceToken, String userData);
    
    /**
     * send the message to the given device
     *
     * @param deviceId the internal id of the device
     * @param message  the message to send
     */
    void sendNotification(String deviceId, PushMessageWrapper message);
    
}
