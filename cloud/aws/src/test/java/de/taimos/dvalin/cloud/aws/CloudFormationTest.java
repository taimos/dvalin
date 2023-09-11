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

import com.amazonaws.services.cloudformation.AmazonCloudFormationClient;
import com.amazonaws.services.cloudformation.model.ResourceSignalStatus;
import com.amazonaws.services.cloudformation.model.SignalResourceRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Field;
import java.util.HashMap;

@ExtendWith(MockitoExtension.class)
class CloudFormationTest {

    @Mock
    private EC2Context ec2Mock;
    @Mock
    private AmazonCloudFormationClient cfnMock;

    private CloudFormation cloudFormation;

    @BeforeEach
    void setup() throws NoSuchFieldException, IllegalAccessException {
        Mockito.when(this.ec2Mock.getInstanceId()).thenReturn("localId");

        this.cloudFormation = new CloudFormation();

        Field ec2Field = CloudFormation.class.getDeclaredField("ec2Context");
        ec2Field.setAccessible(true);
        ec2Field.set(this.cloudFormation, this.ec2Mock);

        Field cfnField = CloudFormation.class.getDeclaredField("cloudFormationClient");
        cfnField.setAccessible(true);
        cfnField.set(this.cloudFormation, this.cfnMock);
    }

    @Test
    void signalInstanceReady() {
        try {
            this.cloudFormation.signalReady("stack", null);
            Assertions.fail("Null check failed for resource");
        } catch (Exception e) {
            Assertions.assertEquals(IllegalArgumentException.class, e.getClass());
        }

        try {
            this.cloudFormation.signalReady("stack", "");
            Assertions.fail("Empty check failed for resource");
        } catch (Exception e) {
            Assertions.assertEquals(IllegalArgumentException.class, e.getClass());
        }

        try {
            this.cloudFormation.signalReady(null, "resource");
            Assertions.fail("Null check failed for stack");
        } catch (Exception e) {
            Assertions.assertEquals(IllegalArgumentException.class, e.getClass());
        }

        try {
            this.cloudFormation.signalReady("", "resource");
            Assertions.fail("Empty check failed for stack");
        } catch (Exception e) {
            Assertions.assertEquals(IllegalArgumentException.class, e.getClass());
        }

        Mockito.doAnswer((Answer<Object>) invocationOnMock -> {
            SignalResourceRequest req = (SignalResourceRequest) invocationOnMock.getArguments()[0];
            Assertions.assertEquals("stack", req.getStackName());
            Assertions.assertEquals("resource", req.getLogicalResourceId());
            Assertions.assertEquals(ResourceSignalStatus.SUCCESS.toString(), req.getStatus());
            Assertions.assertEquals("localId", req.getUniqueId());
            return null;
        }).when(this.cfnMock).signalResource(Mockito.any(SignalResourceRequest.class));

        this.cloudFormation.signalReady("stack", "resource");
    }

    @Test
    void signalInstanceReadyLocalStack() {
        HashMap<String, String> tags = new HashMap<>();
        tags.put(CloudFormation.TAG_CLOUDFORMATION_LOGICAL_ID, "resourceTag");
        tags.put(CloudFormation.TAG_CLOUDFORMATION_STACK_NAME, "stackTag");
        Mockito.when(this.ec2Mock.getInstanceTags()).thenReturn(tags);

        try {
            this.cloudFormation.signalReady(null);
            Assertions.fail("Null check failed for resource");
        } catch (Exception e) {
            Assertions.assertEquals(IllegalArgumentException.class, e.getClass());
        }

        try {
            this.cloudFormation.signalReady("");
            Assertions.fail("Empty check failed for resource");
        } catch (Exception e) {
            Assertions.assertEquals(IllegalArgumentException.class, e.getClass());
        }

        Mockito.doAnswer((Answer<Object>) invocationOnMock -> {
            SignalResourceRequest req = (SignalResourceRequest) invocationOnMock.getArguments()[0];
            Assertions.assertEquals("stackTag", req.getStackName());
            Assertions.assertEquals("resource", req.getLogicalResourceId());
            Assertions.assertEquals(ResourceSignalStatus.SUCCESS.toString(), req.getStatus());
            Assertions.assertEquals("localId", req.getUniqueId());
            return null;
        }).when(this.cfnMock).signalResource(Mockito.any(SignalResourceRequest.class));

        this.cloudFormation.signalReady("resource");
    }

    @Test
    void signalInstanceReadyLocalStackAndResource() {
        HashMap<String, String> tags = new HashMap<>();
        tags.put(CloudFormation.TAG_CLOUDFORMATION_LOGICAL_ID, "resourceTag");
        tags.put(CloudFormation.TAG_CLOUDFORMATION_STACK_NAME, "stackTag");
        Mockito.when(this.ec2Mock.getInstanceTags()).thenReturn(tags);

        Mockito.doAnswer((Answer<Object>) invocationOnMock -> {
            SignalResourceRequest req = (SignalResourceRequest) invocationOnMock.getArguments()[0];
            Assertions.assertEquals("stackTag", req.getStackName());
            Assertions.assertEquals("resourceTag", req.getLogicalResourceId());
            Assertions.assertEquals(ResourceSignalStatus.SUCCESS.toString(), req.getStatus());
            Assertions.assertEquals("localId", req.getUniqueId());
            return null;
        }).when(this.cfnMock).signalResource(Mockito.any(SignalResourceRequest.class));

        this.cloudFormation.signalReady();
    }

}
