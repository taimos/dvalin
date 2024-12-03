package de.taimos.dvalin.jaxrs;

import de.taimos.dvalin.jaxrs.websocket.WebSocketContextHandler;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.transport.http_jetty.JettyHTTPServerEngine;
import org.apache.cxf.transport.http_jetty.JettyHTTPServerEngineFactory;
import org.apache.cxf.transport.http_jetty.ThreadingParameters;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.annotation.Order;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.security.GeneralSecurityException;
import java.util.List;

/**
 * Copyright 2022 Cinovo AG<br>
 * <br>
 * Spring configuration for JAX-RS server (jetty)
 *
 * @author mweise
 */
@HttpProfile
@Configuration
@ImportResource({"classpath:META-INF/cxf/cxf.xml", "classpath:META-INF/cxf/cxf-servlet.xml", "classpath*:spring/jaxrs-server.xml"})
public class JAXRSServerConfig {
    @Value("${jaxrs.annotation:de.taimos.dvalin.jaxrs.JaxRsComponent}")
    protected String serviceAnnotation;
    @Value("${jaxrs.bindhost:0.0.0.0}")
    protected String host;
    @Value("${jaxrs.bindport:${svc.port:8080}}")
    protected int port;
    @Value("${jaxrs.protocol:http}")
    protected String protocol;
    @Value("${jetty.minThreads:5}")
    protected int minThreads;
    @Value("${jetty.maxThreads:150}")
    protected int maxThreads;
    @Value("${jetty.sendVersion:false}")
    protected boolean sendVersion;
    @Value("${jetty.sessions:false}")
    protected boolean sessionSupport;

    @Bean(name = "classesProvider")
    public ServiceAnnotationClassesProvider createServiceAnnotationClassesProvider() {
        ServiceAnnotationClassesProvider provider = new ServiceAnnotationClassesProvider();
        try {
            provider.setServiceAnnotation((Class<? extends Annotation>) Class.forName(this.serviceAnnotation));
        } catch (ClassNotFoundException e) {
            throw new BeanCreationException("Failed to load JAX-RS service annotation", e);
        }
        return provider;
    }

    @Bean(name = "cxf-engine")
    public JettyHTTPServerEngineFactory serverEngineFactory(Bus cxf, //
                                                            @Autowired List<Handler> handlers) {
        JAXRSServerFactoryBean serverFactoryBean = new JAXRSServerFactoryBean();
        serverFactoryBean.setBus(cxf);
        JettyHTTPServerEngineFactory engineFactory = serverFactoryBean.getBus().getExtension(JettyHTTPServerEngineFactory.class);
        try {
            this.createServerEngine(engineFactory, handlers);
        } catch (GeneralSecurityException | IOException e) {
            throw new BeanCreationException("Failed to create JettyHTTPServerEngine: ", e);
        }
        return engineFactory;
    }

    protected void createServerEngine(JettyHTTPServerEngineFactory factory, List<Handler> handlers) throws GeneralSecurityException, IOException {
        JettyHTTPServerEngine engine = factory.createJettyHTTPServerEngine(this.host, this.port, this.protocol);
        engine.setThreadingParameters(this.createThreadingParameters());
        engine.setSendServerVersion(this.sendVersion);
        engine.setSessionSupport(this.sessionSupport);
        engine.setHandlers(handlers);
    }

    protected ThreadingParameters createThreadingParameters() {
        ThreadingParameters threadingParams = new ThreadingParameters();
        threadingParams.setMinThreads(this.minThreads);
        threadingParams.setMaxThreads(this.maxThreads);
        return threadingParams;
    }

    protected ContextHandler createResourceContext(String contextPath, Resource base) {
        ContextHandler context = new ContextHandler(contextPath);
        ResourceHandler res = new ResourceHandler();
        res.setBaseResource(base);
        context.setHandler(res);
        return context;
    }

    @Order(1)
    @Bean(name = "web-server-context-static")
    public ContextHandler staticContextHandler() throws IOException {
        return createResourceContext("/static", Resource.newResource("./static"));
    }

    @Order(2)
    @Bean(name = "web-server-context-web-fs")
    public ContextHandler webFSContextHandler() throws IOException {
        return createResourceContext("/", Resource.newResource("./web"));
    }

    @Order(3)
    @Bean(name = "web-server-context-web")
    public ContextHandler webContextHandler() {
        return createResourceContext("/", Resource.newClassPathResource("/web"));
    }

    @Order(10)
    @Bean
    public WebSocketContextHandler websocketContextHandler() {
        return new WebSocketContextHandler();
    }

    @Bean
    @Order(20)
    public DefaultHandler defaultHandler() {
        return new DefaultHandler();
    }
}