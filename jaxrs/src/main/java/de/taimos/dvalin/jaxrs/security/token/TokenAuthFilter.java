package de.taimos.dvalin.jaxrs.security.token;

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

import de.taimos.daemon.spring.conditional.BeanAvailable;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.dvalin.jaxrs.providers.AuthorizationProvider;
import org.apache.cxf.message.Message;
import org.apache.cxf.security.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

@JaxRsComponent
@BeanAvailable(ITokenAuthUserDAO.class)
public class TokenAuthFilter extends AuthorizationProvider {

    @Autowired
    private ITokenAuthUserDAO tokenAuthUserDAO;

    @Override
    protected boolean isAuthorizationMandatory() {
        return false;
    }

    @Override
    protected SecurityContext handleAuthHeader(ContainerRequestContext requestContext, Message msg, String type, String auth) {
        if (auth != null && type.toLowerCase().equals("token")) {
            return this.loginUser(msg, this.tokenAuthUserDAO.getUserByToken(auth));
        }
        return null;
    }

    @Override
    protected SecurityContext handleOther(ContainerRequestContext requestContext, Message msg, HttpHeaders head) {
        return null;
    }
}
