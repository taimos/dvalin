package de.taimos.springcxfdaemon;

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

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.RedirectionException;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.base.Charsets;

import de.taimos.springcxfdaemon.security.SecurityContextUtil;

public class AbstractAPI implements IContextAware {
	
	protected MessageContext context;
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	
	@Value("${server.url:http://localhost:${jaxrs.bindport:${svc.port:8080}}}")
	private String serverURL;
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	@Override
	public void setMessageContext(MessageContext context) {
		this.context = context;
	}
	
	@Override
	public void setHttpServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	@Override
	public void setHttpServletResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	@Deprecated
	protected final SecurityContext getSC() {
		return SecurityContextUtil.getSC();
	}
	
	@Deprecated
	protected final void assertSC() {
		SecurityContextUtil.assertSC();
	}
	
	@Deprecated
	protected final String getUser() {
		return SecurityContextUtil.getUser();
	}
	
	@Deprecated
	protected final boolean hasRole(String role) {
		return SecurityContextUtil.hasRole(role);
	}
	
	@Deprecated
	protected final UUID requestId() {
		return SecurityContextUtil.requestId();
	}
	
	protected final String getFirstHeader(String name) {
		return this.request.getHeader(name);
	}
	
	protected final void redirectPath(String path) {
		this.redirect(this.getServerURL() + path);
	}
	
	protected String getServerURL() {
		return this.serverURL;
	}
	
	protected final void redirect(String uriString) {
		throw new RedirectionException(Status.SEE_OTHER, URI.create(uriString));
	}
	
	protected String getCurrentURIEncoded() {
		try {
			return URLEncoder.encode(this.getCurrentURI(), Charsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			throw new InternalServerErrorException(e);
		}
	}
	
	protected String getCurrentURI() {
		final String path = this.context.getHttpServletRequest().getRequestURI();
		final String query = this.context.getHttpServletRequest().getQueryString();
		if (query != null) {
			return this.getServerURL() + path + "?" + query;
		}
		return this.getServerURL() + path;
	}
	
}