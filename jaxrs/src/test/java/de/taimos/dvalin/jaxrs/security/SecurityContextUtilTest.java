package de.taimos.dvalin.jaxrs.security;

/*
 * #%L
 * JAX-RS support for dvalin using Apache CXF
 * %%
 * Copyright (C) 2015 - 2016 Taimos GmbH
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.SecurityContext;

import org.apache.cxf.common.security.SimplePrincipal;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.taimos.dvalin.jaxrs.monitoring.InvocationInstance;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SecurityContextUtil.class)
public class SecurityContextUtilTest {

    @Test
    public void getSCWithNullInstance() throws Exception {
        MessageContext messageContext = PowerMockito.mock(MessageContext.class);
        PowerMockito.when(messageContext.getSecurityContext()).thenReturn(null);
        PowerMockito.spy(SecurityContextUtil.class);
        PowerMockito.doReturn(messageContext).when(SecurityContextUtil.class, "getContext");

        Assert.assertNull(SecurityContextUtil.getSC());
    }

    @Test
    public void getSCWithValidInstance() throws Exception {
        SecurityContext securityContext = PowerMockito.mock(SecurityContext.class);
        MessageContext messageContext = PowerMockito.mock(MessageContext.class);
        PowerMockito.when(messageContext.getSecurityContext()).thenReturn(securityContext);
        PowerMockito.spy(SecurityContextUtil.class);
        PowerMockito.doReturn(messageContext).when(SecurityContextUtil.class, "getContext");

        Assert.assertEquals(securityContext, SecurityContextUtil.getSC());
    }

    @Test
    public void assertSCWithNullContext() throws Exception {
        try {
            PowerMockito.spy(SecurityContextUtil.class);
            PowerMockito.doReturn(null).when(SecurityContextUtil.class, "getSC");
            SecurityContextUtil.assertSC();
            Assert.fail("Should not get past assertSC");
        } catch (Exception e) {
            Assert.assertEquals(NotAuthorizedException.class, e.getClass());
        }
    }

    @Test
    public void assertSCWithNullPrincipal() throws Exception {
        try {
            SecurityContext securityContext = PowerMockito.mock(SecurityContext.class);
            PowerMockito.when(securityContext.getUserPrincipal()).thenReturn(null);

            PowerMockito.spy(SecurityContextUtil.class);
            PowerMockito.doReturn(securityContext).when(SecurityContextUtil.class, "getSC");

            SecurityContextUtil.assertSC();
            Assert.fail("Should not get past assertSC");
        } catch (Exception e) {
            Assert.assertEquals(NotAuthorizedException.class, e.getClass());
        }
    }

    @Test
    public void assertSCWithSetPrincipal() throws Exception {
        try {
            SecurityContext securityContext = PowerMockito.mock(SecurityContext.class);
            PowerMockito.when(securityContext.getUserPrincipal()).thenReturn(new SimplePrincipal("foobar"));
            PowerMockito.spy(SecurityContextUtil.class);
            PowerMockito.doReturn(securityContext).when(SecurityContextUtil.class, "getSC");

            SecurityContextUtil.assertSC();
        } catch (Exception e) {
            Assert.fail("Should not fail on assertSC");
        }
    }

    @Test
    public void assertLoggedInWithNullContext() throws Exception {
        try {
            PowerMockito.spy(SecurityContextUtil.class);
            PowerMockito.doReturn(null).when(SecurityContextUtil.class, "getSC");
            SecurityContextUtil.assertLoggedIn();
            Assert.fail("Should not get past assertLoggedIn");
        } catch (Exception e) {
            Assert.assertEquals(NotAuthorizedException.class, e.getClass());
        }
    }

    @Test
    public void assertLoggedInWithNullPrincipal() throws Exception {
        try {
            SecurityContext securityContext = PowerMockito.mock(SecurityContext.class);
            PowerMockito.when(securityContext.getUserPrincipal()).thenReturn(null);

            PowerMockito.spy(SecurityContextUtil.class);
            PowerMockito.doReturn(securityContext).when(SecurityContextUtil.class, "getSC");

            SecurityContextUtil.assertLoggedIn();
            Assert.fail("Should not get past assertLoggedIn");
        } catch (Exception e) {
            Assert.assertEquals(NotAuthorizedException.class, e.getClass());
        }
    }

    @Test
    public void assertLoggedInWithSetPrincipal() throws Exception {
        SecurityContext securityContext = PowerMockito.mock(SecurityContext.class);
        PowerMockito.when(securityContext.getUserPrincipal()).thenReturn(new SimplePrincipal("foobar"));

        PowerMockito.spy(SecurityContextUtil.class);
        PowerMockito.doReturn(securityContext).when(SecurityContextUtil.class, "getSC");

        SecurityContextUtil.assertLoggedIn();
    }

    @Test
    public void getUserWithNullContext() throws Exception {
        PowerMockito.spy(SecurityContextUtil.class);
        PowerMockito.doReturn(null).when(SecurityContextUtil.class, "getSC");
        Assert.assertNull(SecurityContextUtil.getUser());
    }

    @Test
    public void getUserWithNullPrincipal() throws Exception {
        SecurityContext securityContext = PowerMockito.mock(SecurityContext.class);
        PowerMockito.when(securityContext.getUserPrincipal()).thenReturn(null);

        PowerMockito.spy(SecurityContextUtil.class);
        PowerMockito.doReturn(securityContext).when(SecurityContextUtil.class, "getSC");

        Assert.assertNull(SecurityContextUtil.getUser());
    }

    @Test
    public void getUserWithSetPrincipal() throws Exception {
        SecurityContext securityContext = PowerMockito.mock(SecurityContext.class);
        PowerMockito.when(securityContext.getUserPrincipal()).thenReturn(new SimplePrincipal("foobar"));

        PowerMockito.spy(SecurityContextUtil.class);
        PowerMockito.doReturn(securityContext).when(SecurityContextUtil.class, "getSC");

        Assert.assertEquals("foobar", SecurityContextUtil.getUser());
    }

    @Test
    public void hasRoleWithNullContext() throws Exception {
        PowerMockito.spy(SecurityContextUtil.class);
        PowerMockito.doReturn(null).when(SecurityContextUtil.class, "getSC");
        Assert.assertFalse(SecurityContextUtil.hasRole("foo"));
    }

    @Test
    public void hasRoleWithRolePresent() throws Exception {
        SecurityContext securityContext = PowerMockito.mock(SecurityContext.class);
        PowerMockito.when(securityContext.isUserInRole("foo")).thenReturn(true);
        PowerMockito.spy(SecurityContextUtil.class);
        PowerMockito.doReturn(securityContext).when(SecurityContextUtil.class, "getSC");

        Assert.assertTrue(SecurityContextUtil.hasRole("foo"));
    }

    @Test
    public void hasRoleWithRoleAbsent() throws Exception {
        SecurityContext securityContext = PowerMockito.mock(SecurityContext.class);
        PowerMockito.when(securityContext.isUserInRole("foo")).thenReturn(false);
        PowerMockito.spy(SecurityContextUtil.class);
        PowerMockito.doReturn(securityContext).when(SecurityContextUtil.class, "getSC");

        Assert.assertFalse(SecurityContextUtil.hasRole("foo"));
    }

    @Test
    public void requestIdWithNullInstance() throws Exception {
        MessageContext messageContext = PowerMockito.mock(MessageContext.class);
        PowerMockito.when(messageContext.getContent(InvocationInstance.class)).thenReturn(null);
        PowerMockito.spy(SecurityContextUtil.class);
        PowerMockito.doReturn(messageContext).when(SecurityContextUtil.class, "getContext");

        try {
            SecurityContextUtil.requestId();
            Assert.fail("Should not get past requestId");
        } catch (Exception e) {
            Assert.assertEquals(WebApplicationException.class, e.getClass());
            Assert.assertEquals(500, ((WebApplicationException) e).getResponse().getStatus());
        }
    }

    @Test
    public void requestIdWithValidInstance() throws Exception {
        InvocationInstance ii = new InvocationInstance(UUID.randomUUID().toString(), "/");
        MessageContext messageContext = PowerMockito.mock(MessageContext.class);
        PowerMockito.when(messageContext.getContent(InvocationInstance.class)).thenReturn(ii);
        PowerMockito.spy(SecurityContextUtil.class);
        PowerMockito.doReturn(messageContext).when(SecurityContextUtil.class, "getContext");

        Assert.assertEquals(ii.getMessageId(), SecurityContextUtil.requestId());
    }

    @Test
    public void isLoggedInWithNullContext() throws Exception {
        PowerMockito.spy(SecurityContextUtil.class);
        PowerMockito.doReturn(null).when(SecurityContextUtil.class, "getSC");
        Assert.assertFalse(SecurityContextUtil.isLoggedIn());
    }

    @Test
    public void isLoggedInWithNullPrincipal() throws Exception {
        SecurityContext securityContext = PowerMockito.mock(SecurityContext.class);
        PowerMockito.when(securityContext.getUserPrincipal()).thenReturn(null);

        PowerMockito.spy(SecurityContextUtil.class);
        PowerMockito.doReturn(securityContext).when(SecurityContextUtil.class, "getSC");

        Assert.assertFalse(SecurityContextUtil.isLoggedIn());
    }

    @Test
    public void isLoggedInWithSetPrincipal() throws Exception {
        SecurityContext securityContext = PowerMockito.mock(SecurityContext.class);
        PowerMockito.when(securityContext.getUserPrincipal()).thenReturn(new SimplePrincipal("foobar"));

        PowerMockito.spy(SecurityContextUtil.class);
        PowerMockito.doReturn(securityContext).when(SecurityContextUtil.class, "getSC");

        Assert.assertTrue(SecurityContextUtil.isLoggedIn());
    }

}
