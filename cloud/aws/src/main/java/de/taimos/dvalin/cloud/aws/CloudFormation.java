package de.taimos.dvalin.cloud.aws;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.cloudformation.AmazonCloudFormationClient;
import com.amazonaws.services.cloudformation.model.ResourceSignalStatus;
import com.amazonaws.services.cloudformation.model.SignalResourceRequest;
import com.google.common.base.Preconditions;

@Service
public class CloudFormation {

    public static final String TAG_CLOUDFORMATION_LOGICAL_ID = "aws:cloudformation:logical-id";
    public static final String TAG_CLOUDFORMATION_STACK_NAME = "aws:cloudformation:stack-name";

    @AWSClient
    private AmazonCloudFormationClient cloudFormation;

    @Autowired
    private EC2Context ec2Context;

    /**
     * signal success to the given CloudFormation stack.<br>
     * <br>
     * Needed AWS actions:
     * <ul>
     * <li>cloudformation:SignalResource</li>
     * </ul>
     */
    public void signalReady(String stackName, String resourceName) {
        Preconditions.checkArgument(stackName != null && !stackName.isEmpty());
        Preconditions.checkArgument(resourceName != null && !resourceName.isEmpty());
        SignalResourceRequest req = new SignalResourceRequest();
        req.setLogicalResourceId(resourceName);
        req.setStackName(stackName);
        req.setStatus(ResourceSignalStatus.SUCCESS);
        req.setUniqueId(this.ec2Context.getInstanceId());
        this.cloudFormation.signalResource(req);
    }

    /**
     * signal success to the current CloudFormation stack.<br>
     * <br>
     * Needed AWS actions:
     * <ul>
     * <li>ec2:DescribeInstances</li>
     * <li>cloudformation:SignalResource</li>
     * </ul>
     *
     * @param resourceName the resource to signal
     */
    public void signalReady(String resourceName) {
        Preconditions.checkArgument(resourceName != null && !resourceName.isEmpty());
        Map<String, String> hostTags = this.ec2Context.getInstanceTags();
        this.signalReady(hostTags.get(TAG_CLOUDFORMATION_STACK_NAME), resourceName);
    }

    /**
     * signal success to the current CloudFormation stack.<br>
     * The resource is derived from the instance tags
     * <br>
     * Needed AWS actions:
     * <ul>
     * <li>ec2:DescribeInstances</li>
     * <li>cloudformation:SignalResource</li>
     * </ul>
     */
    public void signalReady() {
        Map<String, String> hostTags = this.ec2Context.getInstanceTags();
        this.signalReady(hostTags.get(TAG_CLOUDFORMATION_STACK_NAME), hostTags.get(TAG_CLOUDFORMATION_LOGICAL_ID));
    }

}
