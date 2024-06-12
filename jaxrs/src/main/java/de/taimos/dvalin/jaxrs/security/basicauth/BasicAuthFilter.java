package de.taimos.dvalin.jaxrs.security.basicauth;

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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

import org.apache.commons.codec.binary.Base64;
import org.apache.cxf.message.Message;
import org.apache.cxf.security.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;

import de.taimos.daemon.spring.conditional.BeanAvailable;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.dvalin.jaxrs.providers.AuthorizationProvider;

@JaxRsComponent
@BeanAvailable(IBasicAuthUserDAO.class)
public class BasicAuthFilter extends AuthorizationProvider {

    @Autowired
    private IBasicAuthUserDAO basicAuthUserDAO;

    @Override
    protected boolean isAuthorizationMandatory() {
        return false;
    }

    @Override
    protected SecurityContext handleAuthHeader(ContainerRequestContext requestContext, Message msg, String type, String auth) {
        if (auth != null && type.equalsIgnoreCase("basic")) {
            String decoded = new String(Base64.decodeBase64(auth), StandardCharsets.UTF_8);
            if (!decoded.contains(":")) {
                return null;
            }
            String username = decoded.substring(0, decoded.indexOf(':'));
            String pwd = decoded.substring(decoded.indexOf(':') + 1);

            return this.loginUser(msg, this.basicAuthUserDAO.getUserByNameAndPassword(username, pwd));
        }
        return null;
    }

    @Override
    protected SecurityContext handleOther(ContainerRequestContext requestContext, Message msg, HttpHeaders head) {
        return null;
    }
}
