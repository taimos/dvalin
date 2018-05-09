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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.taimos.daemon.DaemonProperties;
import de.taimos.dvalin.jaxrs.JaxRsComponent;
import de.taimos.dvalin.jaxrs.SpringCXFProperties;
import de.taimos.dvalin.jaxrs.URLUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.config.FilterFactory;
import io.swagger.config.SwaggerConfig;
import io.swagger.core.filter.SpecFilter;
import io.swagger.core.filter.SwaggerSpecFilter;
import io.swagger.jaxrs.Reader;
import io.swagger.jaxrs.config.DefaultReaderConfig;
import io.swagger.models.Info;
import io.swagger.models.Scheme;
import io.swagger.models.Swagger;
import io.swagger.util.Yaml;

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
    private SwaggerConfig config;

    private final AtomicReference<Swagger> swaggerCache = new AtomicReference<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiListing.class);

    protected synchronized Swagger scan() {
        Set<Class<?>> classes = this.scanner.classes();
        if (classes != null) {
            final DefaultReaderConfig rc = new DefaultReaderConfig();
            rc.setScanAllResources(true);

            Reader reader = new Reader(null, rc);
            Swagger swagger = reader.read(classes);
            this.configureServerURL(swagger);
            swagger.info(this.createInfo());
            if (this.config != null) {
                swagger = this.config.configure(swagger);
            }
            this.swaggerCache.compareAndSet(null, swagger);
        }
        return this.swaggerCache.get();
    }

    private void configureServerURL(Swagger swagger) {
        String serverUrl = System.getProperty(SpringCXFProperties.SERVER_URL, "http://localhost:" + System.getProperty(SpringCXFProperties.JAXRS_BINDPORT, System.getProperty("svc.port", "8080")));
        URLUtils.SplitURL split = URLUtils.splitURL(serverUrl);
        swagger.scheme(Scheme.forValue(split.getScheme()));
        swagger.host(split.getHost() + ":" + split.getPort());
        swagger.basePath(System.getProperty(SpringCXFProperties.JAXRS_PATH));
    }

    private Info createInfo() {
        Info info = new Info();
        info.title(System.getProperty(DaemonProperties.SERVICE_NAME, ""));
        String version = this.getClass().getPackage().getImplementationVersion();
        info.version(version != null ? version : "0.0");
        return info;
    }

    private Swagger process(HttpHeaders headers, UriInfo uriInfo) {
        Swagger swagger = this.swaggerCache.get();
        if (swagger == null) {
            swagger = this.scan();
        }
        if (swagger != null) {
            SwaggerSpecFilter filterImpl = FilterFactory.getFilter();
            if (filterImpl != null) {
                SpecFilter f = new SpecFilter();
                swagger = f.filter(swagger, filterImpl, this.getQueryParams(uriInfo.getQueryParameters()), this.getCookies(headers), this.getHeaders(headers));
            }
        }
        return swagger;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, "application/yaml"})
    @ApiOperation(value = "The swagger definition in either JSON or YAML", hidden = true)
    @Path("/swagger.{type:json|yaml}")
    public Response getListing(@Context HttpHeaders headers, @Context UriInfo uriInfo, @PathParam("type") String type) {
        if (StringUtils.isNotBlank(type) && type.trim().equalsIgnoreCase("yaml")) {
            return this.getListingYaml(headers, uriInfo);
        }
        return this.getListingJson(headers, uriInfo);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/swagger")
    @ApiOperation(value = "The swagger definition in JSON", hidden = true)
    public Response getListingJson(@Context HttpHeaders headers, @Context UriInfo uriInfo) {
        Swagger swagger = this.process(headers, uriInfo);

        if (swagger != null) {
            return Response.ok().entity(swagger).build();
        }
        return Response.status(404).build();
    }

    @GET
    @Produces("application/yaml")
    @Path("/swagger")
    @ApiOperation(value = "The swagger definition in YAML", hidden = true)
    public Response getListingYaml(@Context HttpHeaders headers, @Context UriInfo uriInfo) {
        Swagger swagger = this.process(headers, uriInfo);
        try {
            if (swagger != null) {
                String yaml = Yaml.mapper().writeValueAsString(swagger);
                StringBuilder b = new StringBuilder();
                String[] parts = yaml.split("\n");
                for (String part : parts) {
                    b.append(part);
                    b.append("\n");
                }
                return Response.ok().entity(b.toString()).type("application/yaml").build();
            }
        } catch (Exception e) {
            LOGGER.error("Failed to create YAML", e);
        }
        return Response.status(404).build();
    }

    protected Map<String, List<String>> getQueryParams(MultivaluedMap<String, String> params) {
        Map<String, List<String>> output = new HashMap<>();
        if (params != null) {
            for (String key : params.keySet()) {
                List<String> values = params.get(key);
                output.put(key, values);
            }
        }
        return output;
    }

    protected Map<String, String> getCookies(HttpHeaders headers) {
        Map<String, String> output = new HashMap<>();
        if (headers != null) {
            for (String key : headers.getCookies().keySet()) {
                Cookie cookie = headers.getCookies().get(key);
                output.put(key, cookie.getValue());
            }
        }
        return output;
    }

    protected Map<String, List<String>> getHeaders(HttpHeaders headers) {
        Map<String, List<String>> output = new HashMap<>();
        if (headers != null) {
            for (String key : headers.getRequestHeaders().keySet()) {
                List<String> values = headers.getRequestHeaders().get(key);
                output.put(key, values);
            }
        }
        return output;
    }
}
