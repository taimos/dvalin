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
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Priority;
import jakarta.annotation.security.RolesAllowed;
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

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import de.taimos.dvalin.jaxrs.JaxRsAnnotationScanner;
import de.taimos.dvalin.jaxrs.JaxRsComponent;

@Provider
@JaxRsComponent
@Priority(Priorities.AUTHORIZATION)
public class RolesFilter implements ContainerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RolesFilter.class);


    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Message m = JAXRSUtils.getCurrentMessage();

        final Method method = (Method) m.get("org.apache.cxf.resource.method");
        List<RolesAllowed> list = JaxRsAnnotationScanner.searchForAnnotation(method, RolesAllowed.class);
        final List<String> needed = new ArrayList<>();
        for (RolesAllowed annotation : list) {
            needed.addAll(Lists.newArrayList(annotation.value()));
        }
        if (needed.isEmpty()) {
            // No roles needed
            RolesFilter.LOGGER.debug("No roles needed");
            return;
        }

        if (RolesFilter.LOGGER.isDebugEnabled()) {
            RolesFilter.LOGGER.debug("Needs: {}", Joiner.on(",").join(needed));
        }

        final SecurityContext securityContext = m.get(SecurityContext.class);
        if (securityContext != null) {
            for (final String need : needed) {
                if (securityContext.isUserInRole(need)) {
                    // Let it pass
                    RolesFilter.LOGGER.debug("Passed with role {}", need);
                    return;
                }
            }
        }
        String text = "Missing at least one of the following roles: " + Joiner.on(",").join(needed);
        requestContext.abortWith(Response.status(Status.FORBIDDEN).entity(text).build());
    }

}
