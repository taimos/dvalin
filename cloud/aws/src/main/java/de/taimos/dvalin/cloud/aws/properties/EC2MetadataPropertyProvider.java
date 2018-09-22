package de.taimos.dvalin.cloud.aws.properties;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.SdkClientException;
import com.amazonaws.util.EC2MetadataUtils;

import de.taimos.daemon.properties.IPropertyProvider;

public class EC2MetadataPropertyProvider implements IPropertyProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(EC2MetadataPropertyProvider.class);

    @Override
    public Map<String, String> loadProperties() {
        Map<String, String> data = new HashMap<>();
        try {
            data.put("aws.ec2.instance.id", EC2MetadataUtils.getInstanceId());
            data.put("aws.ec2.instance.type", EC2MetadataUtils.getInstanceType());
            data.put("aws.ec2.privateip", EC2MetadataUtils.getPrivateIpAddress());
            data.put("aws.ec2.ami", EC2MetadataUtils.getAmiId());
            data.put("aws.ec2.az", EC2MetadataUtils.getAvailabilityZone());
            data.put("aws.ec2.region", EC2MetadataUtils.getEC2InstanceRegion());
            data.put("aws.ec2.hostname", EC2MetadataUtils.getLocalHostName());
            data.put("aws.ec2.mac", EC2MetadataUtils.getMacAddress());
            data.put("aws.ec2.account", EC2MetadataUtils.getInstanceInfo().getAccountId());
        } catch (SdkClientException e) {
            LOGGER.debug("Failed to contact EC2 Metadata service", e);
        }
        return data;
    }

}
