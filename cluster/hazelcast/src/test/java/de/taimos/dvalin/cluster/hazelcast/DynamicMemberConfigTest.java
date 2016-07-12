package de.taimos.dvalin.cluster.hazelcast;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

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
