package de.taimos.dvalin.cluster.hazelcast;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.config.TcpIpConfig;

public class DynamicMemberConfig extends TcpIpConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicMemberConfig.class);

    private final ClusterInfoProvider infoProvider;
    private final String clusterName;


    public DynamicMemberConfig(ClusterInfoProvider infoProvider) {
        this.setEnabled(true);
        this.infoProvider = infoProvider;
        this.clusterName = this.infoProvider.getClusterName();
        LOGGER.info("Instance is member of cluster {}", this.clusterName);
    }


    @Override
    public List<String> getMembers() {
        List<String> addresses = this.infoProvider.getClusterMemberAddresses(this.clusterName);
        addresses.forEach(ip->LOGGER.info("Found member of cluster {} with address {}", this.clusterName, ip));
        return addresses;
    }


}
