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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.taimos.dvalin.jaxrs.ServiceAnnotationClassesProvider;
import io.swagger.v3.oas.integration.api.OpenAPIConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiScanner;

/**
 * Copyright 2015 Taimos GmbH<br>
 * <br>
 *
 * @author thoeger
 */
@Component
public class SwaggerScanner implements OpenApiScanner {

    @Autowired
    private ServiceAnnotationClassesProvider annotationProvider;

    @Override
    public void setConfiguration(OpenAPIConfiguration openAPIConfiguration) {

    }

    @Override
    public Set<Class<?>> classes() {
        Set<Class<?>> classes = new HashSet<>();
        for (Class<?> clz : this.annotationProvider.getClasses()) {
            if (!this.hasAnnotation(clz, Provider.class) && clz.isAnnotationPresent(Path.class)) {
                classes.add(clz);
            }
        }
        return classes;
    }

    @Override
    public Map<String, Object> resources() {
        return new HashMap<>();
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

}
