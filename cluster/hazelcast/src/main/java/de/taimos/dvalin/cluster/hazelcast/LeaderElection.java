package de.taimos.dvalin.cluster.hazelcast;

import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;

import de.taimos.daemon.spring.conditional.BeanAvailable;

@Service
@BeanAvailable(HazelcastInstance.class)
class LeaderElection {

    @Autowired
    private HazelcastInstance hazelcastInstance;

    /**
     * @return true if the current node is the leader of the cluster
     */
    public boolean isLeader() {
        Cluster cluster = this.hazelcastInstance.getCluster();
        String localUuid = cluster.getLocalMember().getUuid();
        return cluster.getMembers().stream()
            .map(Member::getUuid)
            .min(String::compareTo)
            .filter(Predicate.isEqual(localUuid))
            .isPresent();
    }

}
