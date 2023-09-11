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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.annotation.PostConstruct;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import de.taimos.daemon.spring.annotations.TestComponent;

@TestComponent
public class TestPushService implements PushService {

    public static class TestDevice {
        private Platform platform;
        private String id;
        private String token;
        private String userData;

        public TestDevice(Platform platform, String id, String token, String userData) {
            this.platform = platform;
            this.id = id;
            this.token = token;
            this.userData = userData;
        }

        public Platform getPlatform() {
            return this.platform;
        }

        public String getId() {
            return this.id;
        }

        public String getToken() {
            return this.token;
        }

        public String getUserData() {
            return this.userData;
        }
    }

    private final Multimap<String, PushMessageWrapper> messages = ArrayListMultimap.create();
    private final List<TestDevice> devices = new ArrayList<>();

    @PostConstruct
    public void init() {
        System.out.println("INIT TEST PUSH SERVICE");
    }

    public void clear() {
        this.messages.clear();
        this.devices.clear();
    }

    public Multimap<String, PushMessageWrapper> getMessages() {
        return this.messages;
    }

    public List<TestDevice> getDevices() {
        return this.devices;
    }

    public void addDevice(Platform platform, String id, String deviceToken, String userData) {
        this.devices.add(new TestDevice(platform, id, deviceToken, userData));
    }

    public TestDevice getDeviceById(String id) {
        for (TestDevice device : this.devices) {
            if (device.getId().equals(id)) {
                return device;
            }
        }
        return null;
    }

    @Override
    public String registerDevice(Platform platform, String deviceToken, String userData) {
        for (TestDevice device : this.devices) {
            if (device.getToken().equals(deviceToken)) {
                return device.getId();
            }
        }
        String newId = UUID.randomUUID().toString();
        this.addDevice(platform, newId, deviceToken, userData);
        return newId;
    }

    @Override
    public void sendNotification(String deviceId, PushMessageWrapper message) {
        this.messages.put(deviceId, message);
    }
}
