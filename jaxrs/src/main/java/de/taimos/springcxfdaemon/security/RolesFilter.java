package de.taimos.springcxfdaemon.security;

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
 * http://www.apache.org/licenses/LICENSE-2.0
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

import javax.annotation.Priority;
import javax.annotation.security.RolesAllowed;
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

@Provider
@Priority(Priorities.AUTHORIZATION)
public class RolesFilter implements ContainerRequestFilter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RolesFilter.class);
	
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		Message m = JAXRSUtils.getCurrentMessage();
		
		final Method method = (Method) m.get("org.apache.cxf.resource.method");
		final String[] needed = this.searchRoles(method);
		if (needed.length == 0) {
			// No roles needed
			RolesFilter.LOGGER.debug("No roles needed");
			return;
		}
		
		RolesFilter.LOGGER.debug("Needs: {}", Joiner.on(",").join(needed));
		
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
	
	private String[] searchRoles(Method method) {
		if (method == null) {
			return new String[0];
		}
		if (method.isAnnotationPresent(RolesAllowed.class)) {
			return method.getAnnotation(RolesAllowed.class).value();
		}
		if (method.getDeclaringClass().getInterfaces().length != 0) {
			final Class<?>[] interfaces = method.getDeclaringClass().getInterfaces();
			final String[] needs = this.searchClassArray(interfaces, method);
			if (needs.length > 0) {
				return needs;
			}
		}
		return new String[0];
	}
	
	private String[] searchClassArray(Class<?>[] classes, Method m) {
		for (final Class<?> iface : classes) {
			try {
				final Method iMeth = iface.getMethod(m.getName(), m.getParameterTypes());
				if (iMeth.isAnnotationPresent(RolesAllowed.class)) {
					return iMeth.getAnnotation(RolesAllowed.class).value();
				}
			} catch (NoSuchMethodException | SecurityException e) {
				// search next
			}
		}
		return new String[0];
	}
}