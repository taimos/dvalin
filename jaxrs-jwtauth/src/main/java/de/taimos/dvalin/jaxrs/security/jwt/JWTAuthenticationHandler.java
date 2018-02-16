package de.taimos.dvalin.jaxrs.security.jwt;

/*-
 * #%L
 * JWT Authetication support for dvalin JAX-RS
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

import java.text.ParseException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

import org.apache.cxf.message.Message;
import org.apache.cxf.security.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;

import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.dvalin.jaxrs.providers.AuthorizationProvider;

/**
 * Created by thoeger on 06.01.16.
 */
@JaxRsComponent
public class JWTAuthenticationHandler extends AuthorizationProvider {

    @Autowired
    private IJWTAuth auth;

    @Override
    protected boolean isAuthorizationMandatory() {
        return false;
    }

    @Override
    protected SecurityContext handleAuthHeader(ContainerRequestContext requestContext, Message msg, String type, String auth) {
        if (type == null || !type.equals("Bearer")) {
            return null;
        }
        try {
            return this.loginUser(msg, this.auth.validateToken(auth));
        } catch (ParseException | IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    protected SecurityContext handleOther(ContainerRequestContext requestContext, Message msg, HttpHeaders head) {
        return null;
    }
}
