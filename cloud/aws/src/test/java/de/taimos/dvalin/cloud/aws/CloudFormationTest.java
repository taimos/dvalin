package de.taimos.dvalin.cloud.aws;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.amazonaws.services.cloudformation.AmazonCloudFormationClient;
import com.amazonaws.services.cloudformation.model.ResourceSignalStatus;
import com.amazonaws.services.cloudformation.model.SignalResourceRequest;

@RunWith(MockitoJUnitRunner.class)
public class CloudFormationTest {

    @Mock
    private EC2Context ec2Mock;
    @Mock
    private AmazonCloudFormationClient cfnMock;

    private CloudFormation cloudFormation;

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        Mockito.when(this.ec2Mock.getInstanceId()).thenReturn("localId");
        Mockito.when(this.ec2Mock.getInstanceTags()).thenReturn(new HashMap<String, String>());

        this.cloudFormation = new CloudFormation();

        Field ec2Field = CloudFormation.class.getDeclaredField("ec2Context");
        ec2Field.setAccessible(true);
        ec2Field.set(this.cloudFormation, this.ec2Mock);

        Field cfnField = CloudFormation.class.getDeclaredField("cloudFormation");
        cfnField.setAccessible(true);
        cfnField.set(this.cloudFormation, this.cfnMock);
    }

    @Test
    public void signalInstanceReady() throws Exception {
        try {
            this.cloudFormation.signalReady("stack", null);
            Assert.fail("Null check failed for resource");
        } catch (Exception e) {
            Assert.assertEquals(IllegalArgumentException.class, e.getClass());
        }

        try {
            this.cloudFormation.signalReady("stack", "");
            Assert.fail("Empty check failed for resource");
        } catch (Exception e) {
            Assert.assertEquals(IllegalArgumentException.class, e.getClass());
        }

        try {
            this.cloudFormation.signalReady(null, "resource");
            Assert.fail("Null check failed for stack");
        } catch (Exception e) {
            Assert.assertEquals(IllegalArgumentException.class, e.getClass());
        }

        try {
            this.cloudFormation.signalReady("", "resource");
            Assert.fail("Empty check failed for stack");
        } catch (Exception e) {
            Assert.assertEquals(IllegalArgumentException.class, e.getClass());
        }

        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                SignalResourceRequest req = (SignalResourceRequest) invocationOnMock.getArguments()[0];
                Assert.assertEquals("stack", req.getStackName());
                Assert.assertEquals("resource", req.getLogicalResourceId());
                Assert.assertEquals(ResourceSignalStatus.SUCCESS.toString(), req.getStatus());
                Assert.assertEquals("localId", req.getUniqueId());
                return null;
            }
        }).when(this.cfnMock).signalResource(Mockito.any(SignalResourceRequest.class));

        this.cloudFormation.signalReady("stack", "resource");
    }

    @Test
    public void signalInstanceReadyLocalStack() throws Exception {
        HashMap<String, String> tags = new HashMap<>();
        tags.put(CloudFormation.TAG_CLOUDFORMATION_LOGICAL_ID, "resourceTag");
        tags.put(CloudFormation.TAG_CLOUDFORMATION_STACK_NAME, "stackTag");
        Mockito.when(this.ec2Mock.getInstanceTags()).thenReturn(tags);

        try {
            this.cloudFormation.signalReady(null);
            Assert.fail("Null check failed for resource");
        } catch (Exception e) {
            Assert.assertEquals(IllegalArgumentException.class, e.getClass());
        }

        try {
            this.cloudFormation.signalReady("");
            Assert.fail("Empty check failed for resource");
        } catch (Exception e) {
            Assert.assertEquals(IllegalArgumentException.class, e.getClass());
        }

        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                SignalResourceRequest req = (SignalResourceRequest) invocationOnMock.getArguments()[0];
                Assert.assertEquals("stackTag", req.getStackName());
                Assert.assertEquals("resource", req.getLogicalResourceId());
                Assert.assertEquals(ResourceSignalStatus.SUCCESS.toString(), req.getStatus());
                Assert.assertEquals("localId", req.getUniqueId());
                return null;
            }
        }).when(this.cfnMock).signalResource(Mockito.any(SignalResourceRequest.class));

        this.cloudFormation.signalReady("resource");
    }

    @Test
    public void signalInstanceReadyLocalStackAndResource() throws Exception {
        HashMap<String, String> tags = new HashMap<>();
        tags.put(CloudFormation.TAG_CLOUDFORMATION_LOGICAL_ID, "resourceTag");
        tags.put(CloudFormation.TAG_CLOUDFORMATION_STACK_NAME, "stackTag");
        Mockito.when(this.ec2Mock.getInstanceTags()).thenReturn(tags);

        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                SignalResourceRequest req = (SignalResourceRequest) invocationOnMock.getArguments()[0];
                Assert.assertEquals("stackTag", req.getStackName());
                Assert.assertEquals("resourceTag", req.getLogicalResourceId());
                Assert.assertEquals(ResourceSignalStatus.SUCCESS.toString(), req.getStatus());
                Assert.assertEquals("localId", req.getUniqueId());
                return null;
            }
        }).when(this.cfnMock).signalResource(Mockito.any(SignalResourceRequest.class));

        this.cloudFormation.signalReady();
    }

}
