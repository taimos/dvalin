package de.taimos.dvalin.cloud.aws;

import com.amazonaws.services.cloudformation.AmazonCloudFormationClient;
import com.amazonaws.services.cloudformation.model.ResourceSignalStatus;
import com.amazonaws.services.cloudformation.model.SignalResourceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CloudFormation {

    @AWSClient
    private AmazonCloudFormationClient cloudFormation;

    @Autowired
    private EC2Context ec2Context;


    /**
     * signal success to the current CloudFormation stack.<br>
     * <br>
     * Needed AWS actions:
     * <ul>
     * <li>ec2:DescribeInstances</li>
     * <li>cloudformation:SignalResource</li>
     * </ul>
     *
     * @param resourceName
     *         the resource to signal
     */
    public void signalReady(String resourceName) {
        SignalResourceRequest req = new SignalResourceRequest();
        req.setLogicalResourceId(resourceName);
        req.setStackName(this.ec2Context.getInstanceTags().get("aws:cloudformation:stack-name"));
        req.setStatus(ResourceSignalStatus.SUCCESS);
        req.setUniqueId(this.ec2Context.getInstanceId());
        this.cloudFormation.signalResource(req);
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
        this.signalReady(this.ec2Context.getInstanceTags().get("aws:cloudformation:logical-id"));
    }


}
