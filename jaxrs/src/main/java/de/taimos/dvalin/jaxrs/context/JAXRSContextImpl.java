package de.taimos.dvalin.jaxrs.context;

/*-
 * #%L
 * JAX-RS support for dvalin using Apache CXF
 * %%
 * Copyright (C) 2015 - 2017 Taimos GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.RedirectionException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.ext.MessageContextImpl;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.base.Charsets;

import de.taimos.dvalin.jaxrs.monitoring.InvocationInstance;
import de.taimos.dvalin.jaxrs.security.IUser;
import de.taimos.restutils.RESTAssert;

@Component
public class JAXRSContextImpl implements DvalinRSContext {
    
    @Value("${server.url:http://localhost:${jaxrs.bindport:${svc.port:8080}}}")
    private String serverURL;
    
    @Override
    public SecurityContext getSC() {
        return this.getMessageContext().getSecurityContext();
    }
    
    @Override
    public void assertLoggedIn() {
        SecurityContext sc = this.getSC();
        if ((sc == null) || (sc.getUserPrincipal() == null)) {
            Response response = Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials or session").build();
            throw new NotAuthorizedException(response);
        }
    }
    
    @Override
    public IUser getCurrentUser() {
        return (IUser) this.getMessageContext().get(IUser.class.getName());
    }
    
    @Override
    public boolean hasRole(String role) {
        SecurityContext sc = this.getSC();
        return sc != null && sc.isUserInRole(role);
    }
    
    @Override
    public UUID getRequestId() {
        final InvocationInstance ii = this.getMessageContext().getContent(InvocationInstance.class);
        RESTAssert.assertNotNull(ii, Response.Status.INTERNAL_SERVER_ERROR);
        return ii.getMessageId();
    }
    
    @Override
    public boolean isLoggedIn() {
        SecurityContext sc = this.getSC();
        return (sc != null) && (sc.getUserPrincipal() != null);
    }
    
    @Override
    public String getFirstHeader(String name) {
        return this.getMessageContext().getHttpServletRequest().getHeader(name);
    }
    
    @Override
    public void redirectPath(String path) {
        this.redirect(this.getServerURL() + path);
    }
    
    @Override
    public String getServerURL() {
        return this.serverURL;
    }
    
    @Override
    public void redirect(String uriString) {
        throw new RedirectionException(Response.Status.SEE_OTHER, URI.create(uriString));
    }
    
    @Override
    public String getCurrentURIEncoded() {
        try {
            return URLEncoder.encode(this.getCurrentURI(), Charsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new InternalServerErrorException(e);
        }
    }
    
    @Override
    public String getCurrentURI() {
        HttpServletRequest request = this.getHttpServletRequest();
        final String path = request.getRequestURI();
        final String query = request.getQueryString();
        if (query != null) {
            return this.getServerURL() + path + "?" + query;
        }
        return this.getServerURL() + path;
    }
    
    @Override
    public HttpServletRequest getHttpServletRequest() {
        return this.getMessageContext().getHttpServletRequest();
    }
    
    @Override
    public HttpServletResponse getHttpServletResponse() {
        return this.getMessageContext().getHttpServletResponse();
    }
    
    @Override
    public MessageContext getMessageContext() {
        return new MessageContextImpl(PhaseInterceptorChain.getCurrentMessage());
    }
    
    @Override
    public String getRemoteAddress() {
        HttpServletRequest req = this.getHttpServletRequest();
        String header = req.getHeader("X-Forwarded-For");
        if (header != null) {
            String[] ips = header.split(",");
            if (ips.length > 0) {
                return ips[0];
            }
        }
        return req.getRemoteAddr();
    }
}
