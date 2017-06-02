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
