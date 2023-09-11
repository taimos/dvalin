/*
 * Copyright (c) 2016. Taimos GmbH http://www.taimos.de
 */

package de.taimos.dvalin.test.jaxrs;

/*-
 * #%L
 * Test support for dvalin
 * %%
 * Copyright (C) 2016 - 2017 Taimos GmbH
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

import java.net.URI;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.UriInfo;

import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.mockito.Mockito;

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
            Mockito.mockStatic(JAXRSUtils.class).when(JAXRSUtils::getCurrentMessage).thenReturn(m);
            return m;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
