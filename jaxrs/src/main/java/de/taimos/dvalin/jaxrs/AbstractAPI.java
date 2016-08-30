package de.taimos.dvalin.jaxrs;

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

import java.util.UUID;

import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.taimos.dvalin.jaxrs.context.DvalinRSContext;

/**
 * @deprecated inject {@link DvalinRSContext} to replace super methods
 */
@Deprecated
public class AbstractAPI {
    
    @Autowired
    protected DvalinRSContext dvalinContext;
    
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Deprecated
    protected final SecurityContext getSC() {
        return this.dvalinContext.getSC();
    }
    
    @Deprecated
    protected final void assertSC() {
        this.dvalinContext.assertLoggedIn();
    }
    
    @Deprecated
    protected final String getUser() {
        return this.dvalinContext.getCurrentUser().getUsername();
    }
    
    @Deprecated
    protected final boolean hasRole(String role) {
        return this.dvalinContext.hasRole(role);
    }
    
    @Deprecated
    protected final UUID requestId() {
        return this.dvalinContext.getRequestId();
    }
    
    protected final String getFirstHeader(String name) {
        return this.dvalinContext.getFirstHeader(name);
    }
    
    protected final void redirectPath(String path) {
        this.dvalinContext.redirectPath(path);
    }
    
    protected final String getServerURL() {
        return this.dvalinContext.getServerURL();
    }
    
    protected final void redirect(String uriString) {
        this.dvalinContext.redirect(uriString);
    }
    
    protected final String getCurrentURIEncoded() {
        return this.dvalinContext.getCurrentURIEncoded();
    }
    
    protected final String getCurrentURI() {
        return this.dvalinContext.getCurrentURI();
    }
    
}
