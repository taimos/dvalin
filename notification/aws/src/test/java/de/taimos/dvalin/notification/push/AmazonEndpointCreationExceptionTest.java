package de.taimos.dvalin.notification.push;

import org.junit.Assert;
import org.junit.Test;

import com.amazonaws.services.sns.model.InvalidParameterException;

public class AmazonEndpointCreationExceptionTest {
    
    @Test
    public void shouldRethrow() throws Exception {
        InvalidParameterException ex = new InvalidParameterException("Invalid parameter: Some other reason");
        AmazonEndpointCreationException aece = new AmazonEndpointCreationException(ex);
    
        Assert.assertFalse(aece.didAlreadyExist());
        Assert.assertNotNull(aece.getOriginalException());
    }
    
    @Test
    public void shouldReturnExistingARN() throws Exception {
        InvalidParameterException ex = new InvalidParameterException("Invalid parameter: Token Reason: Endpoint arn:aws:sns:eu-central-1:292004443359:endpoint/GCM/CycleballEU/367b766c-62cc-358b-adf5-b0f8c587ebad already exists with the same Token, but different attributes. (Service: AmazonSNS; Status Code: 400; Error Code: InvalidParameter; Request ID: 5d9a21dc-8f3c-57ca-ba6a-71a0c75ff128)");
        AmazonEndpointCreationException aece = new AmazonEndpointCreationException(ex);
    
        Assert.assertTrue(aece.didAlreadyExist());
        Assert.assertEquals("arn:aws:sns:eu-central-1:292004443359:endpoint/GCM/CycleballEU/367b766c-62cc-358b-adf5-b0f8c587ebad",aece.getExistingARN());
    }
}
