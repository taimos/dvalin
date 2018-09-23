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

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import de.taimos.daemon.DaemonProperties;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.dvalin.jaxrs.ServiceAnnotationClassesProvider;
import de.taimos.dvalin.jaxrs.SpringCXFProperties;
import io.swagger.v3.jaxrs2.Reader;
import io.swagger.v3.oas.annotations.Operation;
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

    private final ServiceAnnotationClassesProvider annotationProvider;

    private final AtomicReference<OpenAPI> swaggerCache = new AtomicReference<>();

    private OpenApiModification config;

    @Autowired
    public ApiListing(ServiceAnnotationClassesProvider annotationProvider) {
        this.annotationProvider = annotationProvider;
    }

    @Autowired(required = false)
    public void setConfig(OpenApiModification config) {
        this.config = config;
    }

    protected synchronized OpenAPI scan() {
        Set<Class<?>> classes = this.classes();
        if (classes != null) {
            Reader reader = new Reader();
            OpenAPI openAPI = reader.read(classes);
            this.configureServerURL(openAPI);
            openAPI.info(this.createInfo());
            if (this.config != null) {
                this.config.reconfigure(openAPI);
            }
            this.swaggerCache.compareAndSet(null, openAPI);
        }
        return this.swaggerCache.get();
    }

    public Set<Class<?>> classes() {
        Set<Class<?>> classes = new HashSet<>();
        for (Class<?> clz : this.annotationProvider.getClasses()) {
            if (!this.hasAnnotation(clz, Provider.class) && clz.isAnnotationPresent(Path.class)) {
                classes.add(clz);
            }
        }
        return classes;
    }

    private boolean hasAnnotation(Class<?> clz, Class<? extends Annotation> ann) {
        if (clz.isAnnotationPresent(ann)) {
            return true;
        }
        for (Class<?> iface : clz.getInterfaces()) {
            if (this.hasAnnotation(iface, ann)) {
                return true;
            }
        }
        return (clz.getSuperclass() != null) && this.hasAnnotation(clz.getSuperclass(), ann);
    }

    private void configureServerURL(OpenAPI openAPI) {
        String serverUrl = System.getProperty(SpringCXFProperties.SERVER_URL, "http://localhost:" + System.getProperty(SpringCXFProperties.JAXRS_BINDPORT, System.getProperty("svc.port", "8080")));
        serverUrl += "/" + System.getProperty(SpringCXFProperties.JAXRS_PATH, "");
        openAPI.addServersItem(new Server().url(serverUrl));
    }

    private Info createInfo() {
        Info info = new Info();
        info.title(System.getProperty(DaemonProperties.SERVICE_NAME, ""));
        String version = this.getClass().getPackage().getImplementationVersion();
        info.version(version != null ? version : "0.0");
        return info;
    }

    private OpenAPI process() {
        OpenAPI openAPI = this.swaggerCache.get();
        if (openAPI == null) {
            openAPI = this.scan();
        }
        return openAPI;
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
        OpenAPI openAPI = this.process();
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
        OpenAPI openAPI = this.process();
        if (openAPI != null) {
            return Response.ok().entity(openAPI).type("application/yaml").build();
        }
        return Response.status(404).build();
    }

}
