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

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.SecurityContext;

import org.apache.cxf.jaxrs.ext.MessageContext;

import de.taimos.dvalin.jaxrs.security.IUser;

/**
 * This class provides access to the current request context
 */
public interface DvalinRSContext {
    
    /**
     * @return the current {@link SecurityContext}
     */
    SecurityContext getSC();
    
    /**
     * assert that the current request is logged in. Otherwise throws a 401 error
     */
    void assertLoggedIn();
    
    /**
     * @return the current user for this request
     */
    IUser getCurrentUser();
    
    /**
     * @param role the role to check
     * @return true if the current user has the given role
     */
    boolean hasRole(String role);
    
    /**
     * @return the unique id of the current reuqest
     */
    UUID getRequestId();
    
    /**
     * @return true if the current request is logged in
     */
    boolean isLoggedIn();
    
    /**
     * @param name the header to fetch
     * @return the first value of the given header
     */
    String getFirstHeader(String name);
    
    /**
     * same as <i>redirect(serverURL + path)</i>
     *
     * @param path the path to redirect to
     */
    void redirectPath(String path);
    
    /**
     * @return the base URL of the application
     */
    String getServerURL();
    
    /**
     * redirect the caller to the given URI
     *
     * @param uriString the target URI
     */
    void redirect(String uriString);
    
    /**
     * @return encoded String containing the current URI
     */
    String getCurrentURIEncoded();
    
    /**
     * @return the current URI of the request
     */
    String getCurrentURI();
    
    /**
     * @return the {@link MessageContext} of the current request
     */
    MessageContext getMessageContext();
    
    /**
     * @return the {@link HttpServletRequest} of the current request
     */
    HttpServletRequest getHttpServletRequest();
    
    /**
     * @return the {@link HttpServletResponse} of the current request
     */
    HttpServletResponse getHttpServletResponse();
    
    /**
     * @return the remote IP of the client. This parses the 'X-Forwarded-For' header when using a proxy.
     */
    String getRemoteAddress();
    
}
