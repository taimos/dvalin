/*
 * Copyright (c) 2016. Taimos GmbH http://www.taimos.de
 */

package de.taimos.dvalin.test.jaxrs;

import java.net.URI;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.UriInfo;

import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

public class ContextMockUtil {
	
	public static ContainerRequestContext mockContainerRequestContext(String requestURL) {
		UriInfo uriInfo = Mockito.mock(UriInfo.class);
		Mockito.when(uriInfo.getBaseUri()).thenReturn(URI.create(requestURL));
		ContainerRequestContext ctx = Mockito.mock(ContainerRequestContext.class);
		Mockito.when(ctx.getUriInfo()).thenReturn(uriInfo);
		return ctx;
	}
	
	public static Message mockCurrentMessage() {
		try {
			Message m = Mockito.spy(new MessageImpl());
			PowerMockito.mockStatic(JAXRSUtils.class);
			PowerMockito.when(JAXRSUtils.class, "getCurrentMessage").thenReturn(m);
			return m;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
