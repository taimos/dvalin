/**
 *
 */
package de.taimos.dvalin.jaxrs.security;

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

import java.util.UUID;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.ext.MessageContextImpl;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.taimos.dvalin.jaxrs.monitoring.InvocationInstance;
import de.taimos.restutils.RESTAssert;

public class SecurityContextUtil {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());


    public static SecurityContext getSC() {
        return SecurityContextUtil.getContext().getSecurityContext();
    }

    public static void assertSC() {
        SecurityContext sc = SecurityContextUtil.getSC();
        if ((sc == null) || (sc.getUserPrincipal() == null)) {
            throw new NotAuthorizedException(Response.status(Status.UNAUTHORIZED).entity("Invalid credentials or session").build());
        }
    }

    public static void assertLoggedIn() {
        SecurityContextUtil.assertSC();
    }

    public static String getUser() {
        SecurityContext sc = SecurityContextUtil.getSC();
        if ((sc != null) && (sc.getUserPrincipal() != null)) {
            return sc.getUserPrincipal().getName();
        }
        return null;
    }

    public static IUser getUserObject() {
        return (IUser) SecurityContextUtil.getContext().get(IUser.class.getName());
    }

    public static boolean hasRole(String role) {
        SecurityContext sc = SecurityContextUtil.getSC();
        return sc != null && sc.isUserInRole(role);
    }

    public static UUID requestId() {
        final InvocationInstance ii = SecurityContextUtil.getContext().getContent(InvocationInstance.class);
        RESTAssert.assertNotNull(ii, Status.INTERNAL_SERVER_ERROR);
        return ii.getMessageId();
    }

    public static boolean isLoggedIn() {
        return SecurityContextUtil.getUser() != null;
    }

    private static MessageContext getContext() {
        return new MessageContextImpl(PhaseInterceptorChain.getCurrentMessage());
    }
}
