/**
 *
 */
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

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.taimos.daemon.DaemonProperties;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.dvalin.jaxrs.SpringCXFProperties;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.jaxrs2.Reader;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.integration.api.OpenAPIConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

/**
 * Copyright 2015 Taimos GmbH<br>
 * <br>
 *
 * @author thoeger
 */
@Path("/")
@JaxRsComponent
public class ApiListing {

    @Autowired
    private SwaggerScanner scanner;

    @Autowired(required = false)
    private OpenAPIConfiguration config;

    private final AtomicReference<OpenAPI> swaggerCache = new AtomicReference<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiListing.class);

    protected synchronized OpenAPI scan() {
        Set<Class<?>> classes = this.scanner.classes();
        if (classes != null) {
            Reader reader = new Reader();
            if (this.config != null) {
                reader.setConfiguration(this.config);
            }
            OpenAPI swagger = reader.read(classes);
            this.configureServerURL(swagger);
            swagger.info(this.createInfo());
            this.swaggerCache.compareAndSet(null, swagger);
        }
        return this.swaggerCache.get();
    }

    private void configureServerURL(OpenAPI swagger) {
        String serverUrl = System.getProperty(SpringCXFProperties.SERVER_URL, "http://localhost:" + System.getProperty(SpringCXFProperties.JAXRS_BINDPORT, System.getProperty("svc.port", "8080")));
        serverUrl += "/" + System.getProperty(SpringCXFProperties.JAXRS_PATH, "");
        swagger.addServersItem(new Server().url(serverUrl));
    }

    private Info createInfo() {
        Info info = new Info();
        info.title(System.getProperty(DaemonProperties.SERVICE_NAME, ""));
        String version = this.getClass().getPackage().getImplementationVersion();
        info.version(version != null ? version : "0.0");
        return info;
    }

    private OpenAPI process() {
        OpenAPI swagger = this.swaggerCache.get();
        if (swagger == null) {
            swagger = this.scan();
        }
        return swagger;
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
        OpenAPI swagger = this.process();
        if (swagger != null) {
            return Response.ok().entity(swagger).type(MediaType.APPLICATION_JSON_TYPE).build();
        }
        return Response.status(404).build();
    }

    @GET
    @Produces("application/yaml")
    @Path("/{a:swagger|openapi}")
    @Operation(hidden = true)
    public Response getListingYaml() {
        OpenAPI swagger = this.process();
        try {
            if (swagger != null) {
                return Response.ok(Response.Status.OK).entity(Yaml.mapper().writeValueAsString(swagger)).type("application/yaml").build();
            }
        } catch (Exception e) {
            LOGGER.error("Failed to create YAML", e);
        }
        return Response.status(404).build();
    }

}
