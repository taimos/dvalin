package de.taimos.dvalin.jaxrs;

/*-
 * #%L
 * JAX-RS support for dvalin using Apache CXF
 * %%
 * Copyright (C) 2015 - 2017 Taimos GmbH
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
