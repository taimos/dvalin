package de.taimos.dvalin.jaxrs;

import java.lang.annotation.Annotation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import com.fasterxml.jackson.databind.ObjectMapper;


@Configuration
@ImportResource({"classpath*:spring/jaxrs-server.xml", "classpath*:spring/jetty.xml"})
public class JAXRSConfig {

    @Value("${jaxrs.annotation:de.taimos.dvalin.jaxrs.JaxRsComponent}")
    private String serviceAnnotation;


    @Bean(name = "objectMapper")
    public ObjectMapper createMapper() {
        return MapperFactory.createDefault();
    }


    @Bean(name = "classesProvider")
    public ServiceAnnotationClassesProvider createServiceAnnotationClassesProvider() {
        ServiceAnnotationClassesProvider provider = new ServiceAnnotationClassesProvider();
        try {
            provider.setServiceAnnotation((Class<? extends Annotation>) Class.forName(this.serviceAnnotation));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load JAX-RS service annotation", e);
        }
        return provider;
    }

}
