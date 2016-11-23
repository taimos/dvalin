package de.taimos.dvalin.test.jaxrs;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import de.taimos.dvalin.test.AssertErrors;

public class WebAssert {
    
    public static void assertErrorStatus(Response.Status status, AssertErrors.Execute func) {
        WebAssert.assertStatus(status.getStatusCode(), func);
    }
    
    public static void assertUnprocessableEntity(AssertErrors.Execute func) {
        WebAssert.assertStatus(422, func);
    }
    
    public static void assertNotFound(AssertErrors.Execute func) {
        WebAssert.assertErrorStatus(Response.Status.NOT_FOUND, func);
    }
    
    private static void assertStatus(int status, AssertErrors.Execute func) {
        AssertErrors.assertThrows(func, e -> {
            if (e instanceof WebApplicationException) {
                return ((WebApplicationException) e).getResponse().getStatus() == status;
            }
            return false;
        });
    }
    
}
