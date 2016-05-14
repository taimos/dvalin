package de.taimos.dvalin.cloud.aws;

import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.model.ResourceSignalStatus;
import com.amazonaws.services.cloudformation.model.SignalResourceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CloudFormation {

    @AWSClient
    private AmazonCloudFormation cloudFormation;

    @Autowired
    private EC2Context ec2Context;


    public void signalReady(String resourceName) {
        SignalResourceRequest req = new SignalResourceRequest();
        req.setLogicalResourceId(resourceName);
        req.setStackName(this.ec2Context.getInstanceTags().get("aws:cloudformation:stack-name"));
        req.setStatus(ResourceSignalStatus.SUCCESS);
        req.setUniqueId(this.ec2Context.getInstanceId());
        this.cloudFormation.signalResource(req);
    }


    public void signalReady() {
        this.signalReady(this.ec2Context.getInstanceTags().get("aws:cloudformation:logical-id"));
    }


}
