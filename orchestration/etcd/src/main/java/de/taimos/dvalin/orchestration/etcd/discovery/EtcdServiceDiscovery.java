package de.taimos.dvalin.orchestration.etcd.discovery;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.taimos.daemon.DaemonStarter;
import de.taimos.daemon.LifecyclePhase;
import de.taimos.daemon.spring.conditional.OnSystemProperty;
import de.taimos.dvalin.orchestration.core.discovery.ServiceDiscovery;
import de.taimos.dvalin.orchestration.core.discovery.ServiceInstance;
import de.taimos.dvalin.orchestration.core.discovery.ServiceListener;
import mousio.etcd4j.EtcdClient;

@Service
@OnSystemProperty(propertyName = "orchestration.etcd.peers")
public class EtcdServiceDiscovery implements ServiceDiscovery {
    
    public static final Logger LOGGER = LoggerFactory.getLogger(EtcdServiceDiscovery.class);
    
    @Value("${orchestration.etcd.peers}")
    private String peers;
    
    private EtcdClient client;
    
    private final ScheduledExecutorService updateExecutor = Executors.newScheduledThreadPool(1);
    
    @PostConstruct
    public void init() {
        List<URI> uris = Arrays.stream(this.peers.split(",")).map(URI::create).collect(Collectors.toList());
        this.client = new EtcdClient(uris.toArray(new URI[0]));
    }
    
    @Override
    public void registerInstance(LifecyclePhase phase) {
        try {
            ServiceInstance instance = this.createLocalServiceInstance(phase);
            String key = this.getServiceInstanceKey(instance);
            this.client.putDir(key).ttl(65).send();
            this.client.put(this.getInstanceStatusKey(instance), instance.getPhase().name()).send();
            this.client.put(this.getInstanceHostKey(instance), instance.getHost()).send();
            this.updateExecutor.scheduleAtFixedRate(() -> {
                try {
                    this.client.refresh(key, 65).send();
                } catch (IOException e) {
                    LOGGER.warn("Error refreshing service state", e);
                }
            }, 30, 30, TimeUnit.SECONDS);
        } catch (IOException e) {
            LOGGER.warn("Error registering instance", e);
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void updateInstance(LifecyclePhase phase) {
        try {
            ServiceInstance instance = this.createLocalServiceInstance(phase);
            this.client.put(this.getInstanceStatusKey(instance), instance.getPhase().name()).send();
        } catch (IOException e) {
            LOGGER.warn("Error updating instance", e);
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void unregisterInstance() {
        try {
            ServiceInstance instance = this.createLocalServiceInstance();
            this.client.deleteDir(this.getServiceInstanceKey(instance)).send();
            this.updateExecutor.shutdown();
        } catch (IOException e) {
            LOGGER.warn("Error unregistering instance", e);
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void setAdditionalProperties(Map<String, Object> properties) {
        
    }
    
    @Override
    public List<ServiceInstance> getInstancesForService(String serviceName) {
        return null;
    }
    
    @Override
    public Optional<Map<String, Object>> getAdditionalProperties(ServiceInstance instance) {
        return null;
    }
    
    @Override
    public void addListenerForService(String serviceName, ServiceListener listener) {
        
    }
    
    @Override
    public void removeListenerForService(String serviceName, ServiceListener listener) {
        
    }
    
    @Override
    public void addListenerForServiceInstance(String serviceName, String instanceId, ServiceListener listener) {
        
    }
    
    @Override
    public void removeListenerForServiceInstance(String serviceName, String instanceId, ServiceListener listener) {
        
    }
    
    private String getInstanceHostKey(ServiceInstance instance) {
        return this.getServiceInstanceKey(instance) + "/host";
    }
    
    private String getInstanceStatusKey(ServiceInstance instance) {
        return this.getServiceInstanceKey(instance) + "/status";
    }
    
    private String getInstancePropertiesKey(ServiceInstance instance) {
        return this.getServiceInstanceKey(instance) + "/properties";
    }
    
    private String getServiceInstanceKey(ServiceInstance instance) {
        return this.getServiceKey(instance) + "/" + instance.getInstanceId();
    }
    
    private String getServiceKey(ServiceInstance instance) {
        return "dvalin/discovery/" + instance.getServiceName();
    }
    
    private ServiceInstance createLocalServiceInstance() {
        return this.createLocalServiceInstance(LifecyclePhase.STARTED);
    }
    
    private ServiceInstance createLocalServiceInstance(LifecyclePhase phase) {
        return new ServiceInstance(DaemonStarter.getHostname(), DaemonStarter.getDaemonName(), DaemonStarter.getInstanceId(), phase);
    }
}
