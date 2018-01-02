package de.taimos.dvalin.jaxrs.security.jwt.cognito;

import java.util.HashMap;
import java.util.Map;

import de.taimos.dvalin.jaxrs.security.IUser;

public class CognitoUser implements IUser {
    
    private String subject;
    
    private String email;
    private boolean emailVerified;
    
    private String username;
    
    private Map<String, Object> customFields = new HashMap<>();
    
    @Override
    public String getUsername() {
        return null;
    }
    
    @Override
    public String[] getRoles() {
        return new String[0];
    }
    
    public String getSubject() {
        return this.subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getEmail() {
        return this.email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public boolean isEmailVerified() {
        return this.emailVerified;
    }
    
    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public Map<String, Object> getCustomFields() {
        return this.customFields;
    }
    
    public void setCustomFields(Map<String, Object> customFields) {
        this.customFields = customFields;
    }
    
    @Override
    public String toString() {
        return "CognitoUser{" +
            "subject='" + subject + '\'' +
            ", email='" + email + '\'' +
            ", emailVerified=" + emailVerified +
            ", username='" + username + '\'' +
            ", customFields=" + customFields +
            '}';
    }
}
