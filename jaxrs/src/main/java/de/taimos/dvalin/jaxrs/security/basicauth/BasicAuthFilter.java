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

import javax.annotation.PostConstruct;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

import org.apache.commons.codec.binary.Base64;
import org.apache.cxf.message.Message;
import org.apache.cxf.security.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;

import de.taimos.dvalin.daemon.conditional.BeanAvailable;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.dvalin.jaxrs.providers.AuthorizationProvider;
import de.taimos.dvalin.jaxrs.security.IUser;

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
        if (auth != null && type.toLowerCase().equals("basic")) {
            String decoded = new String(Base64.decodeBase64(auth));
            if (decoded == null || !decoded.contains(":")) {
                return null;
            }
            String username = decoded.substring(0, decoded.indexOf(":"));
            String pwd = decoded.substring(decoded.indexOf(":") + 1);

            IUser user = basicAuthUserDAO.getUserByNameAndPassword(username, pwd);
            if (user == null) {
                return null;
            }
            msg.put(IUser.class, user);
            return createSC(user.getUsername(), user.getRoles());
        }
        return null;
    }

    @Override
    protected SecurityContext handleOther(ContainerRequestContext requestContext, Message msg, HttpHeaders head) {
        return null;
    }
}
