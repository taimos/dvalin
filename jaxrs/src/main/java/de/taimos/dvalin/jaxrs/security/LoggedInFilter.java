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

import java.io.IOException;
import java.lang.reflect.Method;

import jakarta.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.security.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.taimos.dvalin.jaxrs.JaxRsAnnotationScanner;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.dvalin.jaxrs.security.annotation.LoggedIn;

@Provider
@JaxRsComponent
@Priority(Priorities.AUTHORIZATION)
public class LoggedInFilter implements ContainerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggedInFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Message m = JAXRSUtils.getCurrentMessage();

        final Method method = (Method) m.get("org.apache.cxf.resource.method");

        if (!JaxRsAnnotationScanner.hasAnnotation(method, LoggedIn.class)) {
            LoggedInFilter.LOGGER.debug("No login mandatory");
            return;
        }

        LoggedInFilter.LOGGER.debug("Login mandatory");
        SecurityContext securityContext = m.get(SecurityContext.class);
        if (securityContext == null || securityContext.getUserPrincipal() == null) {
            requestContext.abortWith(Response.status(Status.UNAUTHORIZED).build());
        }
    }

}
