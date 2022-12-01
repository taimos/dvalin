package de.taimos.dvalin.jaxrs.swagger;

import de.taimos.dvalin.jaxrs.HttpProfile;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * Copyright 2022 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@HttpProfile
@Configuration
public class SwaggerConfig {

    @Value("${jaxrs.path:}")
    protected String path;

    @Order(4)
    @Bean(name = "web-server-context-swagger")
    public ContextHandler swaggerContextHandler() {
        ContextHandler context = new ContextHandler(this.path + "/swagger-ui");
        ResourceHandler res = new ResourceHandler();
        res.setBaseResource(Resource.newClassPathResource("/swagger"));
        context.setHandler(res);
        return context;
    }
}
