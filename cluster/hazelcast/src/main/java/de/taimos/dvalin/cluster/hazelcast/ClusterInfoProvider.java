package de.taimos.dvalin.cluster.hazelcast;

import java.util.List;

public interface ClusterInfoProvider {

    String getClusterName();

    List<String> getClusterMemberAddresses(String clusterName);
}
