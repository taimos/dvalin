package de.taimos.dvalin.orchestration.etcd.config;

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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.taimos.daemon.spring.conditional.OnSystemProperty;
import de.taimos.dvalin.orchestration.core.config.ConfigListener;
import de.taimos.dvalin.orchestration.core.config.GlobalConfiguration;
import de.taimos.dvalin.orchestration.etcd.discovery.EtcdServiceDiscovery;
import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.promises.EtcdResponsePromise;
import mousio.etcd4j.responses.EtcdAuthenticationException;
import mousio.etcd4j.responses.EtcdErrorCode;
import mousio.etcd4j.responses.EtcdException;
import mousio.etcd4j.responses.EtcdKeysResponse;

@Service
@OnSystemProperty(propertyName = "orchestration.etcd.peers")
public class EtcdGlobalConfiguration implements GlobalConfiguration {

    public static final Logger LOGGER = LoggerFactory.getLogger(EtcdServiceDiscovery.class);

    private static final String BASE_KEY = "/dvalin/config";

    @Value("${orchestration.etcd.peers}")
    private String peers;

    private EtcdClient client;

    private ConcurrentMap<String, String> configuration = new ConcurrentHashMap<>();

    private final List<ConfigListener> listeners = new ArrayList<>();

    private final AtomicLong etcdIndex = new AtomicLong(1);

    private final AtomicBoolean running = new AtomicBoolean(true);

    @PostConstruct
    public void init() {
        List<URI> uris = Arrays.stream(this.peers.split(",")).map(URI::create).collect(Collectors.toList());
        this.client = new EtcdClient(uris.toArray(new URI[0]));
        new Thread(() -> {
            while (this.running.get()) {
                LOGGER.debug("Polling for config updates");
                try {
                    EtcdResponsePromise<EtcdKeysResponse> send = this.client.get(BASE_KEY)
                        .waitForChange(this.etcdIndex.get())
                        .timeout(10, TimeUnit.SECONDS)
                        .recursive()
                        .send();

                    this.parseWaitResponse(send);
                } catch (IOException e) {
                    LOGGER.warn("Error waiting for instance updates", e);
                }
            }

        }, "etcd-config-poller").start();
        this.addConfigurationListener(new ConfigListener() {
            @Override
            public void added(String key, String value) {
                EtcdGlobalConfiguration.this.configuration.put(key, value);
            }

            @Override
            public void changed(String key, String oldValue, String newValue) {
                EtcdGlobalConfiguration.this.configuration.put(key, newValue);
            }

            @Override
            public void removed(String key, String lastValue) {
                EtcdGlobalConfiguration.this.configuration.remove(key);
            }
        });

        try {
            EtcdKeysResponse response = this.client.get(BASE_KEY).timeout(10, TimeUnit.SECONDS).send().get();
            response.getNode().getNodes().forEach(node -> {
                String configKey = node.getKey().substring(BASE_KEY.length() + 1);
                LOGGER.debug("Population initial configuration with {} = {}", configKey, node.getValue());
                this.configuration.putIfAbsent(configKey, node.getValue());
            });
        } catch (Exception e) {
            LOGGER.warn("Error fetching instance data", e);
            this.running.set(false);
            throw new RuntimeException(e);
        }
    }

    private void parseWaitResponse(EtcdResponsePromise<EtcdKeysResponse> send) throws IOException {
        try {
			EtcdKeysResponse response = send.get();
			this.etcdIndex.set(response.node.getModifiedIndex() + 1);
			String key = response.node.getKey();
			if (key.startsWith(BASE_KEY)) {
				String configKey = key.substring(BASE_KEY.length() + 1);

				switch (response.action) {
				case set:
				case create:
				case update:
				case compareAndSwap:
					if (response.getPrevNode() != null) {
						this.getListeners().forEach(l -> l.changed(configKey, response.getPrevNode().getValue(), response.getNode().getValue()));
					} else {
						this.getListeners().forEach(l -> l.added(configKey, response.getNode().getValue()));
					}
					break;
				case delete:
				case expire:
				case compareAndDelete:
					this.getListeners().forEach(l -> l.removed(configKey, response.getPrevNode().getValue()));
					break;
				case get:
					// Does not happen
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
				this.etcdIndex.set(e.getIndex());
			} else {
				LOGGER.warn("ETCD error", e);
			}
		}
    }

    @PreDestroy
    public void shutdown() {
        this.listeners.clear();
        this.running.set(false);
    }

    @Override
    public void setConfiguration(String key, String value) {
        try {
            this.client.put(BASE_KEY + "/" + key, value).timeout(10, TimeUnit.SECONDS).send().get();
        } catch (Exception e) {
            LOGGER.warn("Error setting configuration data", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setConfiguration(String key, String value, Integer ttlSeconds) {
        try {
            this.client.put(BASE_KEY + "/" + key, value).ttl(ttlSeconds).timeout(10, TimeUnit.SECONDS).send().get();
        } catch (Exception e) {
            LOGGER.warn("Error setting configuration data", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeConfiguration(String key) {
        try {
            this.client.delete(BASE_KEY + "/" + key).timeout(10, TimeUnit.SECONDS).send().get();
        } catch (Exception e) {
            LOGGER.warn("Error removing configuration data", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getConfiguration(String key) {
        return this.configuration.get(key);
    }

    @Override
    public void addConfigurationListener(ConfigListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeConfigurationListener(ConfigListener listener) {
        this.listeners.remove(listener);
    }

    private Collection<ConfigListener> getListeners() {
        return new ArrayList<>(this.listeners);
    }
}
