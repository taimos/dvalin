/**
 *
 */
package de.taimos.dvalin.jaxrs.providers;

/*
 * #%L
 * Daemon with Spring and CXF
 * %%
 * Copyright (C) 2013 - 2015 Taimos GmbH
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

import java.io.IOException;
import java.security.Principal;

import jakarta.annotation.Priority;
import javax.security.auth.Subject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.apache.cxf.common.security.SimplePrincipal;
import org.apache.cxf.interceptor.security.DefaultSecurityContext;
import org.apache.cxf.jaxrs.impl.HttpHeadersImpl;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.security.SecurityContext;

import de.taimos.daemon.DaemonProperties;
import de.taimos.dvalin.jaxrs.security.IUser;
import de.taimos.httputils.WSConstants;

@Provider
@Priority(Priorities.AUTHENTICATION)
public abstract class AuthorizationProvider implements ContainerRequestFilter {

    /**
     * UserName used for anonymous {@link SecurityContext}
     */
    public static final String ANONYMOUS_USER = "ANONYMOUS";


    @Override
    public final void filter(ContainerRequestContext requestContext) throws IOException {
        Message m = JAXRSUtils.getCurrentMessage();

        HttpHeaders head = new HttpHeadersImpl(m);
        String authHeader = head.getHeaderString(WSConstants.HEADER_AUTHORIZATION);
        if ((authHeader != null) && !authHeader.isEmpty() && (authHeader.contains(" "))) {
            int index = authHeader.indexOf(' ');
            String type = authHeader.substring(0, index);
            String auth = authHeader.substring(index + 1);
            SecurityContext sc = this.handleAuthHeader(requestContext, m, type, auth);
            if (sc != null) {
                m.put(SecurityContext.class, sc);
            }
            if(sc == null && this.isAuthorizationMandatory()) {
                this.abortUnauthorized(requestContext);
            }
            return;
        }

        SecurityContext sc = this.handleOther(requestContext, m, head);
        if (sc != null) {
            m.put(SecurityContext.class, sc);
            return;
        }

        if (this.isAuthorizationMandatory()) {
            this.abortUnauthorized(requestContext);
        }
    }

    protected final SecurityContext loginUser(Message msg, IUser user) {
        if (user == null) {
            return null;
        }
        msg.put(IUser.class, user);
        return createSC(user.getUsername(), user.getRoles());
    }

    protected final void abortUnauthorized(ContainerRequestContext requestContext) {
        if (this.sendWWWAuthenticate()) {
            String realm = String.format("Basic realm=\"%s\"", System.getProperty(DaemonProperties.SERVICE_NAME));
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).header(HttpHeaders.WWW_AUTHENTICATE, realm).build());
        } else {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }

    protected boolean sendWWWAuthenticate() {
        return false;
    }

    /**
     * @return <code>true</code> if the request should fail if no valid user is found
     */
    protected abstract boolean isAuthorizationMandatory();

    /**
     * handle the presence of the Authorization header
     *
     * @param requestContext the CXF request context
     * @param msg            the message
     * @param type           the Authorization type (Basic|Bearer|...)
     * @param auth           the auth part of the header
     * @return the {@link SecurityContext} if logged in or null
     */
    protected abstract SecurityContext handleAuthHeader(ContainerRequestContext requestContext, Message msg, String type, String auth);

    /**
     * handle other auth methods like sessions, custom headers, etc
     *
     * @param requestContext the CXF request context
     * @param msg            the message
     * @param head           the HTTP headers
     * @return the {@link SecurityContext} if logged in or null
     */
    protected abstract SecurityContext handleOther(ContainerRequestContext requestContext, Message msg, HttpHeaders head);

    /**
     * Create a {@link SecurityContext} to return to the provider
     *
     * @param user  the user principal
     * @param roles the roles of the user
     * @return the {@link SecurityContext}
     */
    protected static SecurityContext createSC(String user, String... roles) {
        final Subject subject = new Subject();

        final Principal principal = new SimplePrincipal(user);
        subject.getPrincipals().add(principal);

        if (roles != null) {
            for (final String role : roles) {
                subject.getPrincipals().add(new SimplePrincipal(role));
            }
        }
        return new DefaultSecurityContext(principal, subject);
    }

    /**
     * Create a {@link SecurityContext} for an unauthenticated user to return to the provider
     *
     * @param roles the roles of the user
     * @return the {@link SecurityContext}
     */
    protected static SecurityContext createAnonymousSC(String... roles) {
        return AuthorizationProvider.createSC(AuthorizationProvider.ANONYMOUS_USER, roles);
    }

}
