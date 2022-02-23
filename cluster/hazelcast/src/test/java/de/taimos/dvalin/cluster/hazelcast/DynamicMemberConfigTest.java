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

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DynamicMemberConfigTest {

    @Mock private ClusterInfoProvider provider;


    @Test
    public void initLoadsServerGroup() throws Exception {
        String groupName = "myGroup";

        Mockito.when(this.provider.getClusterName()).thenReturn(groupName);
        DynamicMemberConfig cfg = new DynamicMemberConfig(this.provider);

        Mockito.verify(this.provider, Mockito.atLeastOnce()).getClusterName();

        Field field = DynamicMemberConfig.class.getDeclaredField("clusterName");
        field.setAccessible(true);
        Assert.assertEquals(groupName, field.get(cfg));
    }


    @Test
    public void getMembers() throws Exception {
        String groupName = "myGroup";
        String memberAddress = "192.168.1.1";

        Mockito.when(this.provider.getClusterName()).thenReturn(groupName);
        Mockito.when(this.provider.getClusterMemberAddresses(groupName)).thenReturn(Collections.singletonList(memberAddress));
        DynamicMemberConfig cfg = new DynamicMemberConfig(this.provider);
        Mockito.verify(this.provider, Mockito.atLeastOnce()).getClusterName();

        List<String> members = cfg.getMembers();
        Assert.assertEquals(1, members.size());
        Assert.assertEquals(memberAddress, members.get(0));

        Mockito.verify(this.provider, Mockito.atLeastOnce()).getClusterMemberAddresses(groupName);
    }

}
