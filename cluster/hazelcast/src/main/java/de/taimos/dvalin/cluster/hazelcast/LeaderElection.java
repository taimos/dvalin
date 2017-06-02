package de.taimos.dvalin.cluster.hazelcast;

/*-
 * #%L
 * Dvalin Hazelcast support
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
