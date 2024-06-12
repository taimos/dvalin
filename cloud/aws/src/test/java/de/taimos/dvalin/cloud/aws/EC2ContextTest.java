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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class EC2ContextTest {

    private EC2Context context;
    @Mock
    private AmazonEC2Client ec2Mock;
    @Mock
    private AmazonAutoScalingClient asgMock;

    static final private String instanceId = "instanceId";


    @BeforeAll
    static void setupStaticMock() {
        Mockito.mockStatic(EC2MetadataUtils.class)
            .when(EC2MetadataUtils::getInstanceId).thenReturn(EC2ContextTest.instanceId);
    }

    @BeforeEach
    void setUp() throws Exception {
        this.context = new EC2Context();

        Field ec2Field = EC2Context.class.getDeclaredField("ec2Client");
        ec2Field.setAccessible(true);
        ec2Field.set(this.context, this.ec2Mock);

        Field asgField = EC2Context.class.getDeclaredField("autoScalingClient");
        asgField.setAccessible(true);
        asgField.set(this.context, this.asgMock);
    }

    @Test
    void getHostId() {
        Assertions.assertEquals("instanceId", this.context.getInstanceId());
    }

    @Test
    void getHostTags() {
        String tagName = "foo";
        String tagValue = "bar";

        DescribeInstancesResult res = new DescribeInstancesResult();
        Instance instance = new Instance().withInstanceId(EC2ContextTest.instanceId)
            .withTags(new Tag(tagName, tagValue));
        res.withReservations(new Reservation().withInstances(instance));
        Mockito.when(this.ec2Mock.describeInstances(Mockito.any(DescribeInstancesRequest.class))).thenReturn(res);

        Map<String, String> tags = this.context.getInstanceTags();
        Assertions.assertEquals(1, tags.size());
        Assertions.assertTrue(tags.containsKey(tagName));
        Assertions.assertEquals(tagValue, tags.get(tagName));
    }

    @Test()
    void testInvalidResponsesWithMultipleReservations() {
        DescribeInstancesResult res = new DescribeInstancesResult();
        Instance instance = new Instance().withInstanceId(EC2ContextTest.instanceId);
        res.withReservations(new Reservation().withInstances(instance), new Reservation().withInstances(instance));
        Mockito.when(this.ec2Mock.describeInstances(Mockito.any(DescribeInstancesRequest.class))).thenReturn(res);

        Assertions.assertThrows(IllegalStateException.class, () -> this.context.getInstanceTags());
    }

    @Test()
    void testInvalidResponsesWithMissingReservations() {
        DescribeInstancesResult res = new DescribeInstancesResult();
        Mockito.when(this.ec2Mock.describeInstances(Mockito.any(DescribeInstancesRequest.class))).thenReturn(res);

        Assertions.assertThrows(IllegalStateException.class, () -> this.context.getInstanceTags());
    }

    @Test()
    void testInvalidResponsesWithMultipleInstances() {
        DescribeInstancesResult res = new DescribeInstancesResult();
        Instance instance = new Instance().withInstanceId("instanceId");
        Instance instance2 = new Instance().withInstanceId("instanceId2");
        res.withReservations(new Reservation().withInstances(instance, instance2));
        Mockito.when(this.ec2Mock.describeInstances(Mockito.any(DescribeInstancesRequest.class))).thenReturn(res);

        Assertions.assertThrows(IllegalStateException.class, () -> this.context.getInstanceTags());
    }

    @Test()
    void testInvalidResponsesWithMissingInstances() {
        DescribeInstancesResult res = new DescribeInstancesResult();
        res.withReservations(new Reservation().withInstances());
        Mockito.when(this.ec2Mock.describeInstances(Mockito.any(DescribeInstancesRequest.class))).thenReturn(res);

        Assertions.assertThrows(IllegalStateException.class, () -> this.context.getInstanceTags());
    }

    @Test
    void getServerGroupName() {
        AutoScalingInstanceDetails inst = new AutoScalingInstanceDetails().withAutoScalingGroupName("awsGroup");
        DescribeAutoScalingInstancesResult res = new DescribeAutoScalingInstancesResult();
        res.withAutoScalingInstances(inst);
        Mockito.when(this.asgMock.describeAutoScalingInstances(Mockito.any(DescribeAutoScalingInstancesRequest.class)))
            .thenReturn(res);

        Assertions.assertEquals("awsGroup", this.context.getAutoScalingGroup());
    }

    @Test()
    void getServerGroupNameWithInvalidResponseMultipleInstances() {
        AutoScalingInstanceDetails inst = new AutoScalingInstanceDetails().withAutoScalingGroupName("awsGroup");
        AutoScalingInstanceDetails inst2 = new AutoScalingInstanceDetails().withAutoScalingGroupName("awsGroup");
        DescribeAutoScalingInstancesResult res = new DescribeAutoScalingInstancesResult();
        res.withAutoScalingInstances(inst, inst2);
        Mockito.when(this.asgMock.describeAutoScalingInstances(Mockito.any(DescribeAutoScalingInstancesRequest.class)))
            .thenReturn(res);

        Assertions.assertThrows(IllegalStateException.class, () -> this.context.getAutoScalingGroup());
    }

    @Test()
    void getServerGroupNameWithInvalidResponseMissingInstance() {
        DescribeAutoScalingInstancesResult res = new DescribeAutoScalingInstancesResult();
        res.withAutoScalingInstances();
        Mockito.when(this.asgMock.describeAutoScalingInstances(Mockito.any(DescribeAutoScalingInstancesRequest.class)))
            .thenReturn(res);

        Assertions.assertThrows(IllegalStateException.class, () -> this.context.getAutoScalingGroup());
    }

    @Test
    void getServerGroupTags() {
        try {
            this.context.getAutoScalingGroupTags(null);
            Assertions.fail("Null check failed");
        } catch (Exception e) {
            Assertions.assertEquals(IllegalArgumentException.class, e.getClass());
        }

        try {
            this.context.getAutoScalingGroupTags("");
            Assertions.fail("Empty check failed");
        } catch (Exception e) {
            Assertions.assertEquals(IllegalArgumentException.class, e.getClass());
        }

        String tagName = "foo";
        String tagValue = "bar";

        AutoScalingGroup grp = new AutoScalingGroup();
        grp.setAutoScalingGroupName("awsGroup");
        grp.withTags(new TagDescription().withKey(tagName).withValue(tagValue));
        DescribeAutoScalingGroupsResult res = new DescribeAutoScalingGroupsResult();
        res.setAutoScalingGroups(Collections.singleton(grp));
        Mockito.when(this.asgMock.describeAutoScalingGroups(Mockito.any(DescribeAutoScalingGroupsRequest.class)))
            .thenReturn(res);

        Map<String, String> tags = this.context.getAutoScalingGroupTags("awsGroup");
        Assertions.assertEquals(1, tags.size());
        Assertions.assertTrue(tags.containsKey(tagName));
        Assertions.assertEquals(tagValue, tags.get(tagName));
    }

    @Test
    void getServerGroupMembers() {
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
        Mockito.when(this.asgMock.describeAutoScalingGroups(Mockito.any(DescribeAutoScalingGroupsRequest.class)))
            .thenReturn(res);

        try {
            this.context.getAutoScalingMembers(null);
            Assertions.fail("Null check failed");
        } catch (Exception e) {
            Assertions.assertEquals(IllegalArgumentException.class, e.getClass());
        }

        try {
            this.context.getAutoScalingMembers("");
            Assertions.fail("Empty check failed");
        } catch (Exception e) {
            Assertions.assertEquals(IllegalArgumentException.class, e.getClass());
        }

        List<String> groupMembers = this.context.getAutoScalingMembers("awsGroup");
        Assertions.assertEquals(1, groupMembers.size());
        Assertions.assertEquals("healthyOne", groupMembers.get(0));
    }

    @Test()
    void getServerGroupMembersWithInvalidResponseMultipleGroups() {
        AutoScalingGroup grp = new AutoScalingGroup().withAutoScalingGroupName("awsGroup");
        AutoScalingGroup grp2 = new AutoScalingGroup().withAutoScalingGroupName("awsGroup2");
        DescribeAutoScalingGroupsResult res = new DescribeAutoScalingGroupsResult();
        res.withAutoScalingGroups(grp, grp2);
        Mockito.when(this.asgMock.describeAutoScalingGroups(Mockito.any(DescribeAutoScalingGroupsRequest.class)))
            .thenReturn(res);

        Assertions.assertThrows(IllegalStateException.class, () -> this.context.getAutoScalingMembers("awsGroup"));
    }

    @Test()
    void getServerGroupMembersWithInvalidResponseMissingGroups() {
        DescribeAutoScalingGroupsResult res = new DescribeAutoScalingGroupsResult();
        res.withAutoScalingGroups();
        Mockito.when(this.asgMock.describeAutoScalingGroups(Mockito.any(DescribeAutoScalingGroupsRequest.class)))
            .thenReturn(res);

        Assertions.assertThrows(IllegalStateException.class, () -> this.context.getAutoScalingMembers("awsGroup"));
    }

    @Test
    void getServerGroupMemberAddresses() {
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

        Mockito.doReturn(Collections.singletonList("localId")).when(this.context)
            .getAutoScalingMembers(Mockito.anyString());

        try {
            this.context.getPrivateAutoScalingMemberIPs(null);
            Assertions.fail("Null check failed");
        } catch (Exception e) {
            Assertions.assertEquals(IllegalArgumentException.class, e.getClass());
        }

        try {
            this.context.getPrivateAutoScalingMemberIPs("");
            Assertions.fail("Empty check failed");
        } catch (Exception e) {
            Assertions.assertEquals(IllegalArgumentException.class, e.getClass());
        }

        List<String> memberAddresses = this.context.getPrivateAutoScalingMemberIPs("awsGroup");
        Assertions.assertEquals(1, memberAddresses.size());
        Assertions.assertEquals("192.168.1.1", memberAddresses.get(0));
    }

}
