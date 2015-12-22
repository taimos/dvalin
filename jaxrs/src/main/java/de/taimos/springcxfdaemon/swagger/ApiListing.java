/**
 * 
 */
package de.taimos.springcxfdaemon.swagger;

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
import de.taimos.springcxfdaemon.JaxRsComponent;
import de.taimos.springcxfdaemon.SpringCXFProperties;
import de.taimos.springcxfdaemon.URLUtils;
import de.taimos.springcxfdaemon.URLUtils.SplitURL;
import io.swagger.annotations.ApiOperation;
import io.swagger.config.FilterFactory;
import io.swagger.config.SwaggerConfig;
import io.swagger.core.filter.SpecFilter;
import io.swagger.core.filter.SwaggerSpecFilter;
import io.swagger.jaxrs.Reader;
import io.swagger.jaxrs.config.DefaultReaderConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.models.Info;
import io.swagger.models.Scheme;
import io.swagger.models.Swagger;
import io.swagger.util.Yaml;

/**
 * Copyright 2015 Taimos GmbH<br>
 * <br>
 *
 * @author thoeger
 * 		
 */
@Path("/")
@JaxRsComponent
public class ApiListing {
	
	Logger LOGGER = LoggerFactory.getLogger(ApiListingResource.class);
	
	@Autowired
	private SwaggerScanner scanner;
	@Autowired(required = false)
	private SwaggerConfig config;
	
	private AtomicReference<Swagger> swaggerCache = new AtomicReference<>();
	
	
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
		SplitURL split = URLUtils.splitURL(System.getProperty(SpringCXFProperties.SERVER_URL, "localhost"));
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
			e.printStackTrace();
		}
		return Response.status(404).build();
	}
	
	protected Map<String, List<String>> getQueryParams(MultivaluedMap<String, String> params) {
		Map<String, List<String>> output = new HashMap<String, List<String>>();
		if (params != null) {
			for (String key : params.keySet()) {
				List<String> values = params.get(key);
				output.put(key, values);
			}
		}
		return output;
	}
	
	protected Map<String, String> getCookies(HttpHeaders headers) {
		Map<String, String> output = new HashMap<String, String>();
		if (headers != null) {
			for (String key : headers.getCookies().keySet()) {
				Cookie cookie = headers.getCookies().get(key);
				output.put(key, cookie.getValue());
			}
		}
		return output;
	}
	
	protected Map<String, List<String>> getHeaders(HttpHeaders headers) {
		Map<String, List<String>> output = new HashMap<String, List<String>>();
		if (headers != null) {
			for (String key : headers.getRequestHeaders().keySet()) {
				List<String> values = headers.getRequestHeaders().get(key);
				output.put(key, values);
			}
		}
		return output;
	}
}
