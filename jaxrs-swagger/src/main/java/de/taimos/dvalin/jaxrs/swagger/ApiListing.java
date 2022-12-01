package de.taimos.dvalin.jaxrs.swagger;

/*
 * #%L
 * JAX-RS support for dvalin using Apache CXF
 * %%
 * Copyright (C) 2015 Taimos GmbH
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

import de.taimos.dvalin.jaxrs.HttpProfile;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.models.OpenAPI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Copyright 2015 Taimos GmbH<br>
 * <br>
 *
 * @author thoeger
 */
@Path("/")
@HttpProfile
@JaxRsComponent
public class ApiListing {

    private final OpenAPIProvider openAPIProvider;

    @Autowired
    public ApiListing(OpenAPIProvider openAPIProvider) {
        this.openAPIProvider = openAPIProvider;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, "application/yaml"})
    @Operation(hidden = true)
    @Path("/{a:swagger|openapi}.{type:json|yaml}")
    public Response getListing(@PathParam("type") String type) {
        if (StringUtils.isNotBlank(type) && type.trim().equalsIgnoreCase("yaml")) {
            return this.getListingYaml();
        }
        return this.getListingJson();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{a:swagger|openapi}")
    @Operation(hidden = true)
    public Response getListingJson() {
        OpenAPI openAPI = this.openAPIProvider.process();
        if (openAPI != null) {
            return Response.ok().entity(openAPI).type(MediaType.APPLICATION_JSON_TYPE).build();
        }
        return Response.status(404).build();
    }

    @GET
    @Produces("application/yaml")
    @Path("/{a:swagger|openapi}")
    @Operation(hidden = true)
    public Response getListingYaml() {
        OpenAPI openAPI = this.openAPIProvider.process();
        if (openAPI != null) {
            return Response.ok().entity(openAPI).type("application/yaml").build();
        }
        return Response.status(404).build();
    }
}
