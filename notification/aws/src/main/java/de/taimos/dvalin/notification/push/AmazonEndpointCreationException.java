package de.taimos.dvalin.notification.push;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amazonaws.services.sns.model.InvalidParameterException;

public class AmazonEndpointCreationException {
    
    private final InvalidParameterException ex;
    private final String existingARN;
    
    public AmazonEndpointCreationException(InvalidParameterException ex) {
        this.ex = ex;
        
        Pattern p = Pattern.compile(".*Endpoint (arn:aws:sns[^ ]+) already exists with the same Token.*");
        Matcher m = p.matcher(ex.getErrorMessage());
        if (m.matches()) {
            this.existingARN = m.group(1);
        } else {
            this.existingARN = null;
        }
    }
    
    public boolean didAlreadyExist() {
        return this.existingARN != null;
    }
    
    public InvalidParameterException getOriginalException() {
        return this.ex;
    }
    
    public String getExistingARN() {
        return this.existingARN;
    }
}
