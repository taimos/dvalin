package de.taimos.dvalin.notification.push;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.CreatePlatformEndpointResult;
import com.amazonaws.services.sns.model.InvalidParameterException;
import com.amazonaws.services.sns.model.PublishRequest;

import de.taimos.daemon.spring.annotations.ProdComponent;
import de.taimos.dvalin.cloud.aws.AWSClient;

/**
 * PushService implementation using AWS SNS
 * <br>
 * Needed AWS actions:
 * <ul>
 * <li>sns:CreatePlatformEndpoint</li>
 * <li>sns:Publish</li>
 * </ul>
 */
@ProdComponent
public class AmazonSNSPushService implements PushService {
    
    public static final Logger LOGGER = LoggerFactory.getLogger(AmazonSNSPushService.class);
    
    private static final String MESSAGE_STRUCTURE_JSON = "json";
    
    @AWSClient
    private AmazonSNSClient snsClient;
    
    @Value("${aws.pushApplicationARN}")
    private String applicationARN;
    
    @Override
    public String registerDevice(String deviceToken, String userData) {
        try {
            LOGGER.info("Creating platform endpoint with device token {}", deviceToken);
            CreatePlatformEndpointRequest cpeReq = new CreatePlatformEndpointRequest()
                .withPlatformApplicationArn(this.applicationARN)
                .withToken(deviceToken);
            
            cpeReq.withCustomUserData(userData);
            CreatePlatformEndpointResult cpeRes = this.snsClient.createPlatformEndpoint(cpeReq);
            return cpeRes.getEndpointArn();
        } catch (InvalidParameterException ipe) {
            String message = ipe.getErrorMessage();
            Pattern p = Pattern.compile(".*Endpoint (arn:aws:sns[^ ]+) already exists with the same token.*");
            Matcher m = p.matcher(message);
            if (m.matches()) {
                // The platform endpoint already exists for this token, but with
                // additional custom data that
                // createEndpoint doesn't want to overwrite. Just use the
                // existing platform endpoint.
                return m.group(1);
            } else {
                // Rethrow the exception, the input is actually bad.
                throw ipe;
            }
        }
    }
    
    @Override
    public void sendNotification(String deviceId, PushMessageWrapper message) {
        try {
            PublishRequest req = new PublishRequest();
            req.setTargetArn(deviceId);
            req.setMessageStructure(MESSAGE_STRUCTURE_JSON);
            req.setMessage(message.toMessage());
            this.snsClient.publish(req);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
