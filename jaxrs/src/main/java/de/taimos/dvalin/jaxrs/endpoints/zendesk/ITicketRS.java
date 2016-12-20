package de.taimos.dvalin.jaxrs.endpoints.zendesk;

public interface ITicketRS {
    
    String getRequesterName();
    
    String getRequesterMail();
    
    String getSubject();
    
    String getBody();
    
}
