package de.taimos.dvalin.orchestration.etcd.discovery;

/*-
 * #%L
 * Dvalin service orchestration with etcd
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
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import de.taimos.daemon.DaemonStarter;
import de.taimos.daemon.LifecyclePhase;
import de.taimos.daemon.spring.conditional.OnSystemProperty;
import de.taimos.dvalin.orchestration.core.discovery.ServiceDiscovery;
import de.taimos.dvalin.orchestration.core.discovery.ServiceInstance;
import de.taimos.dvalin.orchestration.core.discovery.ServiceListener;
import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.promises.EtcdResponsePromise;
import mousio.etcd4j.responses.EtcdAuthenticationException;
import mousio.etcd4j.responses.EtcdErrorCode;
import mousio.etcd4j.responses.EtcdException;
import mousio.etcd4j.responses.EtcdKeysResponse;

@Service
@OnSystemProperty(propertyName = "orchestration.etcd.peers")
public class EtcdServiceDiscovery implements ServiceDiscovery {

    public static final Logger LOGGER = LoggerFactory.getLogger(EtcdServiceDiscovery.class);

    private static final int INSTANCE_TIMEOUT = 16;
    private static final int REFRESH_INTERVAL = 5;

    @Value("${orchestration.etcd.peers}")
    private String peers;

    private EtcdClient client;

    private final ScheduledExecutorService updateExecutor = Executors.newScheduledThreadPool(1);

    private final ObjectMapper mapper = new ObjectMapper();

    private Map<String, Object> properties;

    private final Multimap<String, ServiceListener> serviceListeners = ArrayListMultimap.create();
    private final ConcurrentMap<String, Long> etcdIndex = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        List<URI> uris = Arrays.stream(this.peers.split(",")).map(URI::create).collect(Collectors.toList());
        this.client = new EtcdClient(uris.toArray(new URI[0]));
    }

    @PreDestroy
    public void shutdown() {
        this.serviceListeners.clear();
    }

    @Override
    public void registerInstance() {
        try {
            ServiceInstance instance = this.createLocalServiceInstance();
            String key = this.getServiceInstanceKey(instance);

            this.client.put(key, this.getHostInfoAsString(instance, null)).ttl(INSTANCE_TIMEOUT).send().get();
            this.updateExecutor.scheduleAtFixedRate(() -> {
                try {
                    this.client.refresh(key, INSTANCE_TIMEOUT).send();
                } catch (IOException e) {
                    LOGGER.warn("Error refreshing service state", e);
                }
            }, REFRESH_INTERVAL, REFRESH_INTERVAL, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOGGER.warn("Error registering instance", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateInstance() {
        try {
            ServiceInstance instance = this.createLocalServiceInstance();
            this.client.put(this.getServiceInstanceKey(instance), this.getHostInfoAsString(instance, this.properties)).ttl(INSTANCE_TIMEOUT).prevExist(true).send();
        } catch (IOException e) {
            LOGGER.warn("Error updating instance", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unregisterInstance() {
        try {
            ServiceInstance instance = this.createLocalServiceInstance();
            this.client.delete(this.getServiceInstanceKey(instance)).send();
            this.updateExecutor.shutdown();
        } catch (IOException e) {
            LOGGER.warn("Error unregistering instance", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setAdditionalProperties(Map<String, Object> properties) {
        this.properties = properties;
        this.updateInstance();

    }

    @Override
    public Optional<Map<String, Object>> getAdditionalProperties() {
        return Optional.ofNullable(this.properties);
    }

    @Override
    public List<ServiceInstance> getInstancesForService(String serviceName) {
        List<ServiceInstance> list = new ArrayList<>();
        try {
            String serviceKey = this.getServiceKey(serviceName);
            EtcdKeysResponse response = this.client.get(serviceKey).timeout(5, TimeUnit.SECONDS).send().get();
            Pattern keyPattern = Pattern.compile(serviceKey + "/([A-Fa-f0-9\\-]+)");
            response.getNode().getNodes().forEach(node -> {
                Matcher matcher = keyPattern.matcher(node.getKey());
                if (matcher.matches()) {
                    String instanceId = matcher.group(1);
                    HostInfo hostInfo = this.parseHostInfo(node.getValue());
                    list.add(new ServiceInstance(hostInfo.getHost(), serviceName, instanceId, LifecyclePhase.valueOf(hostInfo.getStatus())));
                }
            });
        } catch (Exception e) {
            LOGGER.warn("Error fetching instance data", e);
        }
        return list;
    }

    @Override
    public Optional<Map<String, Object>> getAdditionalProperties(ServiceInstance instance) {
        try {
            EtcdKeysResponse response = this.client.get(this.getServiceInstanceKey(instance)).timeout(5, TimeUnit.SECONDS).send().get();
            String value = response.getNode().getValue();
            if (value != null) {
                return Optional.ofNullable(this.parseHostInfo(value).getProperties());
            }
        } catch (Exception e) {
            LOGGER.warn("Error fetching instance data", e);
        }
        return Optional.empty();
    }

    @Override
    public void addListenerForService(String serviceName, ServiceListener listener) {
        boolean hadListeners = this.serviceListeners.containsKey(serviceName);

        this.serviceListeners.put(serviceName, listener);

        if (!hadListeners) {
            new Thread(() -> {
                String serviceKey = this.getServiceKey(serviceName);
                Pattern keyPattern = Pattern.compile(serviceKey + "/([A-Fa-f0-9\\-]+)");
                while (this.serviceListeners.containsKey(serviceName)) {
                    LOGGER.debug("Polling for service updates for service {}", serviceName);
                    try {
                        EtcdResponsePromise<EtcdKeysResponse> send = this.client.get(serviceKey)
                            .waitForChange(this.etcdIndex.getOrDefault(serviceName, 1L))
                            .timeout(10, TimeUnit.SECONDS)
                            .recursive()
                            .send();

                        this.parseWaitResponse(serviceName, keyPattern, send);
                    } catch (IOException e) {
                        LOGGER.warn("Error waiting for instance updates", e);
                    }
                }
            }, "etcd-poller-" + serviceName).start();
        }
    }

    private void parseWaitResponse(String serviceName, Pattern keyPattern, EtcdResponsePromise<EtcdKeysResponse> send) throws IOException {
        try {
			EtcdKeysResponse response = send.get();
			this.etcdIndex.put(serviceName, response.node.getModifiedIndex() + 1);
			Matcher matcher = keyPattern.matcher(response.node.getKey());
			if (matcher.matches()) {
				String instanceId = matcher.group(1);

				switch (response.action) {
				case set:
				case create:
				case update:
				case compareAndSwap:
					HostInfo info = this.parseHostInfo(response.getNode().getValue());
					ServiceInstance instance = new ServiceInstance(info.getHost(), serviceName, instanceId, LifecyclePhase.valueOf(info.getStatus()));
					if (response.getPrevNode() != null) {
						this.getListeners(serviceName).forEach(l -> l.instanceChanged(instance));
					} else {
						this.getListeners(serviceName).forEach(l -> l.instanceRegistered(instance));
					}
					break;
				case delete:
				case expire:
				case compareAndDelete:
					HostInfo removedInfo = this.parseHostInfo(response.getPrevNode().getValue());
					ServiceInstance removedInstance = new ServiceInstance(removedInfo.getHost(), serviceName, instanceId, LifecyclePhase.valueOf(removedInfo.getStatus()));
					this.getListeners(serviceName).forEach(l -> l.instanceUnregistered(removedInstance));
					break;
                case get:
                    // Do nothing here
                    break;
				}
			}
		} catch (TimeoutException e) {
			// do nothing and retry
		} catch (EtcdAuthenticationException e) {
			LOGGER.warn("ETCD authentication error", e);
		} catch (EtcdException e) {
			if (e.getErrorCode() == EtcdErrorCode.EventIndexCleared) {
				LOGGER.info("Skipped events as index was outdated");
				this.etcdIndex.put(serviceName, e.getIndex());
			} else {
				LOGGER.warn("ETCD error", e);
			}
		}
    }

    @Override
    public void removeListenerForService(String serviceName, ServiceListener listener) {
        this.serviceListeners.remove(serviceName, listener);
    }

    private String getServiceInstanceKey(ServiceInstance instance) {
        return this.getServiceKey(instance.getServiceName()) + "/" + instance.getInstanceId();
    }

    private String getServiceKey(String serviceName) {
        return "/dvalin/discovery/" + serviceName;
    }

    private ServiceInstance createLocalServiceInstance() {
        return new ServiceInstance(DaemonStarter.getHostname(), DaemonStarter.getDaemonName(), DaemonStarter.getInstanceId(), DaemonStarter.getCurrentPhase());
    }

    private String getHostInfoAsString(ServiceInstance instance, Map<String, Object> properties) {
        try {
            HostInfo info = new HostInfo();
            info.setHost(instance.getHost());
            info.setStatus(instance.getPhase().name());
            info.setProperties(properties);

            return this.mapper.writeValueAsString(info);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private HostInfo parseHostInfo(String info) {
        try {
            return this.mapper.readValue(info, HostInfo.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Collection<ServiceListener> getListeners(String serviceName) {
        return Lists.newArrayList(this.serviceListeners.get(serviceName));
    }
}
