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

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * provide service discovery using the orchestration tooling
 */
public interface ServiceDiscovery {
    
    // methods concering the current service instance
    
    /**
     * register the current instance in the registry
     */
    void registerInstance();
    
    /**
     * update the current instance in the registry
     */
    void updateInstance();
    
    /**
     * remove the current instance from the registry
     */
    void unregisterInstance();
    
    /**
     * set additional properties for the current instance
     *
     * @param properties the properties to attach to the instance
     */
    void setAdditionalProperties(Map<String, Object> properties);
    
    /**
     * @return the current properties of the instance
     */
    Optional<Map<String, Object>> getAdditionalProperties();
    
    // methods to fetch information about other instances
    
    /**
     * fetch a list of all service instances for a given service
     *
     * @param serviceName the name of the service to find
     * @return list of registered instances for the given service
     */
    List<ServiceInstance> getInstancesForService(String serviceName);
    
    /**
     * fetch the additional properties of the given instance if available
     *
     * @param instance the service instance
     * @return the additional properties
     */
    Optional<Map<String, Object>> getAdditionalProperties(ServiceInstance instance);
    
    /**
     * register a listener for changes to service instance
     *
     * @param serviceName the service to register the listener for
     * @param listener    the listener to register
     */
    void addListenerForService(String serviceName, ServiceListener listener);
    
    /**
     * remove the listener
     *
     * @param serviceName the service to remove the listener from
     * @param listener    the listener to remove
     */
    void removeListenerForService(String serviceName, ServiceListener listener);
    
}
