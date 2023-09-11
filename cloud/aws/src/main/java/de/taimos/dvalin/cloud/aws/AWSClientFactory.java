package de.taimos.dvalin.cloud.aws;

import java.lang.reflect.InvocationTargetException;

import jakarta.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.amazonaws.AmazonWebServiceClient;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;

public class AWSClientFactory<T extends AmazonWebServiceClient> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AWSClientFactory.class);

    private String regionName;
    private String endpoint;

    public AWSClientFactory withRegion(@Nullable String regionName) {
        this.regionName = regionName;
        return this;
    }

    public AWSClientFactory withEndpoint(@Nullable String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public T create(Class<T> clientClass) {
        String region = this.getRegion();
        AWSClientFactory.LOGGER.debug("Using AWS region {}", region);

        final AwsClientBuilder clientBuilder;
        try {
            clientBuilder = (AwsClientBuilder) clientClass.getMethod("builder").invoke(null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Failed to construct client builder", e);
        }

        if (StringUtils.hasText(this.endpoint)) {
            AWSClientFactory.LOGGER.debug("Using customer AWS endpoint {}", this.endpoint);
            clientBuilder.setEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(this.endpoint, region));
        } else {
            clientBuilder.setRegion(region);
        }
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        ProxyConfiguration.configure(clientConfiguration);
        clientBuilder.setClientConfiguration(clientConfiguration);
        return (T) clientBuilder.build();
    }

    private String getRegion() {
        if (StringUtils.hasText(this.regionName)) {
            return this.regionName;
        }
        if (System.getProperty("aws.region") != null) {
            return System.getProperty("aws.region");
        }
        if (System.getenv("AWS_DEFAULT_REGION") != null) {
            return System.getenv("AWS_DEFAULT_REGION");
        }
        if (System.getenv("AWS_REGION") != null) {
            return System.getenv("AWS_REGION");
        }
        Region currentRegion = Regions.getCurrentRegion();
        if (currentRegion != null) {
            return currentRegion.getName();
        }
        return Regions.DEFAULT_REGION.getName();
    }

}
