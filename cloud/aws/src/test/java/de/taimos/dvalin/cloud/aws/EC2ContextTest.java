package de.taimos.dvalin.cloud.aws;

/*-
 * #%L
 * Dvalin AWS support
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.amazonaws.services.autoscaling.AmazonAutoScalingClient;
import com.amazonaws.services.autoscaling.model.AutoScalingGroup;
import com.amazonaws.services.autoscaling.model.AutoScalingInstanceDetails;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsRequest;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsResult;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingInstancesRequest;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingInstancesResult;
import com.amazonaws.services.autoscaling.model.TagDescription;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.util.EC2MetadataUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest(EC2MetadataUtils.class)
public class EC2ContextTest {
    
    private EC2Context context;
    @Mock
    private AmazonEC2Client ec2Mock;
    @Mock
    private AmazonAutoScalingClient asgMock;
    
    final private String instanceId = "instanceId";
    
    @Before
    public void setUp() throws Exception {
        this.context = new EC2Context();
        
        Field ec2Field = EC2Context.class.getDeclaredField("ec2Client");
        ec2Field.setAccessible(true);
        ec2Field.set(this.context, this.ec2Mock);
        
        Field asgField = EC2Context.class.getDeclaredField("autoScalingClient");
        asgField.setAccessible(true);
        asgField.set(this.context, this.asgMock);
        
        PowerMockito.mockStatic(EC2MetadataUtils.class);
        PowerMockito.when(EC2MetadataUtils.getInstanceId()).thenReturn(this.instanceId);
    }
    
    @Test
    public void getHostId() {
        Assert.assertEquals("instanceId", this.context.getInstanceId());
    }
    
    @Test
    public void getHostTags() {
        String tagName = "foo";
        String tagValue = "bar";
        
        DescribeInstancesResult res = new DescribeInstancesResult();
        Instance instance = new Instance().withInstanceId(this.instanceId).withTags(new Tag(tagName, tagValue));
        res.withReservations(new Reservation().withInstances(instance));
        Mockito.when(this.ec2Mock.describeInstances(Mockito.any(DescribeInstancesRequest.class))).thenReturn(res);
        
        Map<String, String> tags = this.context.getInstanceTags();
        Assert.assertEquals(1, tags.size());
        Assert.assertTrue(tags.containsKey(tagName));
        Assert.assertEquals(tagValue, tags.get(tagName));
    }
    
    @Test(expected = IllegalStateException.class)
    public void testInvalidResponsesWithMultipleReservations() {
        DescribeInstancesResult res = new DescribeInstancesResult();
        Instance instance = new Instance().withInstanceId(this.instanceId);
        res.withReservations(new Reservation().withInstances(instance), new Reservation().withInstances(instance));
        Mockito.when(this.ec2Mock.describeInstances(Mockito.any(DescribeInstancesRequest.class))).thenReturn(res);
        this.context.getInstanceTags();
    }
    
    @Test(expected = IllegalStateException.class)
    public void testInvalidResponsesWithMissingReservations() {
        DescribeInstancesResult res = new DescribeInstancesResult();
        Mockito.when(this.ec2Mock.describeInstances(Mockito.any(DescribeInstancesRequest.class))).thenReturn(res);
        this.context.getInstanceTags();
    }
    
    @Test(expected = IllegalStateException.class)
    public void testInvalidResponsesWithMultipleInstances() {
        DescribeInstancesResult res = new DescribeInstancesResult();
        Instance instance = new Instance().withInstanceId("instanceId");
        Instance instance2 = new Instance().withInstanceId("instanceId2");
        res.withReservations(new Reservation().withInstances(instance, instance2));
        Mockito.when(this.ec2Mock.describeInstances(Mockito.any(DescribeInstancesRequest.class))).thenReturn(res);
        this.context.getInstanceTags();
    }
    
    @Test(expected = IllegalStateException.class)
    public void testInvalidResponsesWithMissingInstances() {
        DescribeInstancesResult res = new DescribeInstancesResult();
        res.withReservations(new Reservation().withInstances());
        Mockito.when(this.ec2Mock.describeInstances(Mockito.any(DescribeInstancesRequest.class))).thenReturn(res);
        this.context.getInstanceTags();
    }
    
    @Test
    public void getServerGroupName() {
        AutoScalingInstanceDetails inst = new AutoScalingInstanceDetails().withAutoScalingGroupName("awsGroup");
        DescribeAutoScalingInstancesResult res = new DescribeAutoScalingInstancesResult();
        res.withAutoScalingInstances(inst);
        Mockito.when(this.asgMock.describeAutoScalingInstances(Mockito.any(DescribeAutoScalingInstancesRequest.class))).thenReturn(res);
        
        Assert.assertEquals("awsGroup", this.context.getAutoScalingGroup());
    }
    
    @Test(expected = IllegalStateException.class)
    public void getServerGroupNameWithInvalidResponseMultipleInstances() {
        AutoScalingInstanceDetails inst = new AutoScalingInstanceDetails().withAutoScalingGroupName("awsGroup");
        AutoScalingInstanceDetails inst2 = new AutoScalingInstanceDetails().withAutoScalingGroupName("awsGroup");
        DescribeAutoScalingInstancesResult res = new DescribeAutoScalingInstancesResult();
        res.withAutoScalingInstances(inst, inst2);
        Mockito.when(this.asgMock.describeAutoScalingInstances(Mockito.any(DescribeAutoScalingInstancesRequest.class))).thenReturn(res);
        
        this.context.getAutoScalingGroup();
    }
    
    @Test(expected = IllegalStateException.class)
    public void getServerGroupNameWithInvalidResponseMissingInstance() throws Exception {
        DescribeAutoScalingInstancesResult res = new DescribeAutoScalingInstancesResult();
        res.withAutoScalingInstances();
        Mockito.when(this.asgMock.describeAutoScalingInstances(Mockito.any(DescribeAutoScalingInstancesRequest.class))).thenReturn(res);
        
        this.context.getAutoScalingGroup();
    }
    
    @Test
    public void getServerGroupTags() {
        try {
            this.context.getAutoScalingGroupTags(null);
            Assert.fail("Null check failed");
        } catch (Exception e) {
            Assert.assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
        try {
            this.context.getAutoScalingGroupTags("");
            Assert.fail("Empty check failed");
        } catch (Exception e) {
            Assert.assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
        String tagName = "foo";
        String tagValue = "bar";
        
        AutoScalingGroup grp = new AutoScalingGroup();
        grp.setAutoScalingGroupName("awsGroup");
        grp.withTags(new TagDescription().withKey(tagName).withValue(tagValue));
        DescribeAutoScalingGroupsResult res = new DescribeAutoScalingGroupsResult();
        res.setAutoScalingGroups(Collections.singleton(grp));
        Mockito.when(this.asgMock.describeAutoScalingGroups(Mockito.any(DescribeAutoScalingGroupsRequest.class))).thenReturn(res);
        
        Map<String, String> tags = this.context.getAutoScalingGroupTags("awsGroup");
        Assert.assertEquals(1, tags.size());
        Assert.assertTrue(tags.containsKey(tagName));
        Assert.assertEquals(tagValue, tags.get(tagName));
    }
    
    @Test
    public void getServerGroupMembers() {
        Collection<com.amazonaws.services.autoscaling.model.Instance> instances = new ArrayList<>();
        com.amazonaws.services.autoscaling.model.Instance healthyInstance = new com.amazonaws.services.autoscaling.model.Instance();
        healthyInstance.setInstanceId("healthyOne");
        healthyInstance.setHealthStatus("Healthy");
        instances.add(healthyInstance);
        com.amazonaws.services.autoscaling.model.Instance unhealthyInstance = new com.amazonaws.services.autoscaling.model.Instance();
        unhealthyInstance.setInstanceId("unhealthyOne");
        unhealthyInstance.setHealthStatus("Unhealthy");
        instances.add(unhealthyInstance);
        
        AutoScalingGroup grp = new AutoScalingGroup();
        grp.setAutoScalingGroupName("awsGroup");
        grp.setInstances(instances);
        DescribeAutoScalingGroupsResult res = new DescribeAutoScalingGroupsResult();
        res.setAutoScalingGroups(Collections.singleton(grp));
        Mockito.when(this.asgMock.describeAutoScalingGroups(Mockito.any(DescribeAutoScalingGroupsRequest.class))).thenReturn(res);
        
        try {
            this.context.getAutoScalingMembers(null);
            Assert.fail("Null check failed");
        } catch (Exception e) {
            Assert.assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
        try {
            this.context.getAutoScalingMembers("");
            Assert.fail("Empty check failed");
        } catch (Exception e) {
            Assert.assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
        List<String> groupMembers = this.context.getAutoScalingMembers("awsGroup");
        Assert.assertEquals(1, groupMembers.size());
        Assert.assertEquals("healthyOne", groupMembers.get(0));
    }
    
    @Test(expected = IllegalStateException.class)
    public void getServerGroupMembersWithInvalidResponseMultipleGroups() {
        AutoScalingGroup grp = new AutoScalingGroup().withAutoScalingGroupName("awsGroup");
        AutoScalingGroup grp2 = new AutoScalingGroup().withAutoScalingGroupName("awsGroup2");
        DescribeAutoScalingGroupsResult res = new DescribeAutoScalingGroupsResult();
        res.withAutoScalingGroups(grp, grp2);
        Mockito.when(this.asgMock.describeAutoScalingGroups(Mockito.any(DescribeAutoScalingGroupsRequest.class))).thenReturn(res);
        
        this.context.getAutoScalingMembers("awsGroup");
    }
    
    @Test(expected = IllegalStateException.class)
    public void getServerGroupMembersWithInvalidResponseMissingGroups() {
        DescribeAutoScalingGroupsResult res = new DescribeAutoScalingGroupsResult();
        res.withAutoScalingGroups();
        Mockito.when(this.asgMock.describeAutoScalingGroups(Mockito.any(DescribeAutoScalingGroupsRequest.class))).thenReturn(res);
        
        this.context.getAutoScalingMembers("awsGroup");
    }
    
    @Test
    public void getServerGroupMemberAddresses() {
        this.context = Mockito.spy(this.context);
        
        com.amazonaws.services.ec2.model.Instance instance = new com.amazonaws.services.ec2.model.Instance();
        instance.setInstanceId("localId");
        instance.setPrivateIpAddress("192.168.1.1");
        instance.setState(new InstanceState().withName(InstanceStateName.Running));
        
        com.amazonaws.services.ec2.model.Instance brokenInstance = new com.amazonaws.services.ec2.model.Instance();
        brokenInstance.setInstanceId("otherId");
        brokenInstance.setPrivateIpAddress("192.168.1.2");
        brokenInstance.setState(new InstanceState().withName(InstanceStateName.ShuttingDown));
        
        DescribeInstancesResult res = new DescribeInstancesResult();
        res.withReservations(new Reservation().withInstances(instance, brokenInstance));
        Mockito.when(this.ec2Mock.describeInstances(Mockito.any(DescribeInstancesRequest.class))).thenReturn(res);
        
        Mockito.doReturn(Collections.singletonList("localId")).when(this.context).getAutoScalingMembers(Mockito.anyString());
        
        try {
            this.context.getPrivateAutoScalingMemberIPs(null);
            Assert.fail("Null check failed");
        } catch (Exception e) {
            Assert.assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
        try {
            this.context.getPrivateAutoScalingMemberIPs("");
            Assert.fail("Empty check failed");
        } catch (Exception e) {
            Assert.assertEquals(IllegalArgumentException.class, e.getClass());
        }
        
        List<String> memberAddresses = this.context.getPrivateAutoScalingMemberIPs("awsGroup");
        Assert.assertEquals(1, memberAddresses.size());
        Assert.assertEquals("192.168.1.1", memberAddresses.get(0));
    }
    
}
