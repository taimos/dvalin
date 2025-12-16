package de.taimos.dvalin.jaxrs.swagger;

import de.taimos.daemon.DaemonProperties;
import de.taimos.dvalin.jaxrs.HttpProfile;
import de.taimos.dvalin.jaxrs.ServiceAnnotationClassesProvider;
import de.taimos.dvalin.jaxrs.SpringCXFProperties;
import io.swagger.v3.jaxrs2.Reader;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Copyright 2022 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@HttpProfile
public class OpenAPIProvider {
    private final ServiceAnnotationClassesProvider annotationProvider;

    private final AtomicReference<OpenAPI> swaggerCache = new AtomicReference<>();

    @Autowired(required = false)
    private OpenApiModification config;

    @Autowired
    public OpenAPIProvider(ServiceAnnotationClassesProvider annotationProvider) {
        this.annotationProvider = annotationProvider;
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

    protected boolean hasAnnotation(Class<?> clz, Class<? extends Annotation> ann) {
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

    protected void configureServerURL(OpenAPI openAPI) {
        String port = System.getProperty(SpringCXFProperties.JAXRS_BINDPORT, System.getProperty("svc.port", "8080"));
        String serverUrl = System.getProperty(SpringCXFProperties.SERVER_URL, System.getProperty("jaxrs.protocol", "http") + "://localhost:" + port);
        String path = System.getProperty(SpringCXFProperties.JAXRS_PATH, "");
        if (!path.startsWith("/")) {
            serverUrl += "/";
        }
        serverUrl += path;
        openAPI.addServersItem(new Server().url(serverUrl));
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

    protected OpenAPI process() {
        OpenAPI openAPI = this.swaggerCache.get();
        if (openAPI == null) {
            openAPI = this.scan();
        }
        return openAPI;
    }

    protected Info createInfo() {
        Info info = new Info();
        info.title(System.getProperty(DaemonProperties.SERVICE_NAME, ""));
        String version = this.getClass().getPackage().getImplementationVersion();
        info.version(version != null ? version : "0.0");
        return info;
    }
}
