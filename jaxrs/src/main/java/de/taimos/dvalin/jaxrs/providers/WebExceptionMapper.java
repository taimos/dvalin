package de.taimos.dvalin.jaxrs.providers;

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

import jakarta.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
@Priority(Priorities.ENTITY_CODER)
public class WebExceptionMapper implements ExceptionMapper<WebApplicationException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebExceptionMapper.class);


    @Override
    public Response toResponse(WebApplicationException ex) {
        Response r = ex.getResponse();
        if (r == null) {
            r = Response.serverError().build();
        }

        if (this.logError(ex, r)) {
            WebExceptionMapper.LOGGER.warn(ex.getMessage(), ex);
        }

        return r;
    }

    @SuppressWarnings("unused")
    protected boolean logError(WebApplicationException ex, Response r) {
        // Only log server exceptions
        return (r.getStatus() >= 500) && (r.getStatus() <= 599);
    }

}
