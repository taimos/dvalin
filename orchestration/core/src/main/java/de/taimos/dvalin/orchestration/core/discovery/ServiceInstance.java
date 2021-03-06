package de.taimos.dvalin.orchestration.core.discovery;

/*-
 * #%L
 * Dvalin service orchestration
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
    
    @Override
    public String toString() {
        return "ServiceInstance{" +
            "host='" + host + '\'' +
            ", serviceName='" + serviceName + '\'' +
            ", instanceId='" + instanceId + '\'' +
            ", phase=" + phase +
            '}';
    }
}
