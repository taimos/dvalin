package de.taimos.dvalin.cluster.hazelcast;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.config.TopicConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.LifecycleListener;
import com.hazelcast.spring.cache.HazelcastCacheManager;

import de.taimos.daemon.spring.conditional.BeanAvailable;
import de.taimos.daemon.spring.conditional.OnSystemProperty;

@Configuration
public class HazelcastConfig {

    private static final String HAZELCAST_PUBLIC_IP = "HAZELCAST_PUBLIC_IP";

    @Autowired(required = false)
    private List<LifecycleListener> lifecycleListeners;
    @Autowired(required = false)
    private List<ConfigProvider> configProviders;
    
    @Value("${hazelcast.port:}")
    private String hazelcastPort;

    @Value("${hazelcast.portcount:}")
    private String hazelcastPortcount;
    
    @Autowired(required = false)
    private List<QueueConfig> queueConfigs;
    @Autowired(required = false)
    private List<TopicConfig> topicConfigs;
    @Autowired(required = false)
    private List<MapConfig> mapConfigs;

    @Bean
    @BeanAvailable(value = ClusterInfoProvider.class)
    @OnSystemProperty(propertyName = "hazelcast.client")
    public HazelcastInstance createClient(ClusterInfoProvider infoProvider, @Value("${hazelcast.client}") String clusterName) {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.getNetworkConfig().setAddresses(infoProvider.getClusterMemberAddresses(clusterName));
        clientConfig.getSerializationConfig().addSerializerConfig(OptionalSerializer.createConfig());
        HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);
        if (this.lifecycleListeners != null) {
            this.lifecycleListeners.forEach(client.getLifecycleService()::addLifecycleListener);
        }
        return client;
    }

    @Bean
    @BeanAvailable(value = ClusterInfoProvider.class)
    @OnSystemProperty(propertyName = "hazelcast.cluster", propertyValue = "true")
    public HazelcastInstance createCluster(ClusterInfoProvider infoProvider) {
        Config cfg = new Config();

        // Set aggressive timeouts for cloud environment
        cfg.setProperty("hazelcast.max.join.seconds", "15");
        cfg.setProperty("hazelcast.max.no.heartbeat.seconds", "15");
        cfg.setProperty("hazelcast.member.list.publish.interval.seconds", "60");

        NetworkConfig networkConfig = cfg.getNetworkConfig();

        // Check for ip address override (e.g. Docker)
        String hazelcastPublicIp = System.getenv(HAZELCAST_PUBLIC_IP);
        if (hazelcastPublicIp != null) {
            networkConfig.setPublicAddress(hazelcastPublicIp);
        }
    
        if (this.hazelcastPort != null) {
            networkConfig.setPort(Integer.parseInt(this.hazelcastPort));
        }

        if (this.hazelcastPortcount != null) {
            networkConfig.setPortCount(Integer.parseInt(this.hazelcastPortcount));
            networkConfig.setPortAutoIncrement(true);
        } else {
            networkConfig.setPortAutoIncrement(false);
        }
    
        if (this.queueConfigs != null) {
            this.queueConfigs.forEach(cfg::addQueueConfig);
        }
        if (this.topicConfigs != null) {
            this.topicConfigs.forEach(cfg::addTopicConfig);
        }
        if (this.mapConfigs != null) {
            this.mapConfigs.forEach(cfg::addMapConfig);
        }

        JoinConfig joinConfig = networkConfig.getJoin();
        joinConfig.getMulticastConfig().setEnabled(false);
        joinConfig.getAwsConfig().setEnabled(false);
        joinConfig.setTcpIpConfig(new DynamicMemberConfig(infoProvider));

        cfg.getSerializationConfig().addSerializerConfig(OptionalSerializer.createConfig());
    
        if (this.configProviders != null) {
            this.configProviders.forEach(configProvider -> configProvider.configure(cfg));
        }
        
        HazelcastInstance instance = Hazelcast.newHazelcastInstance(cfg);
        if (this.lifecycleListeners != null) {
            this.lifecycleListeners.forEach(instance.getLifecycleService()::addLifecycleListener);
        }
        return instance;
    }

    @Bean
    @BeanAvailable(HazelcastInstance.class)
    CacheManager cacheManager(HazelcastInstance hazelcastInstance) {
        return new HazelcastCacheManager(hazelcastInstance);
    }

}
