package de.taimos.dvalin.jaxrs;

/*
 * #%L
 * JAX-RS support for dvalin using Apache CXF
 * %%
 * Copyright (C) 2015 - 2016 Taimos GmbH
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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public final class JaxRsAnnotationScanner {

    public static final Logger LOGGER = LoggerFactory.getLogger(JaxRsAnnotationScanner.class);

    private JaxRsAnnotationScanner() {
        //
    }

    /**
     * Checks if there is an annotation of the given type on this method or on type level for all interfaces and superclasses
     *
     * @param method     the method to scan
     * @param annotation the annotation to search for
     * @return <i>true</i> if the given annotation is present on method or type level annotations in the type hierarchy
     */
    public static boolean hasAnnotation(Method method, Class<? extends Annotation> annotation) {
        return !searchForAnnotation(method, annotation).isEmpty();
    }

    /**
     * Searches for all annotations of the given type on this method or on type level for all interfaces and superclasses
     *
     * @param method     the method to scan
     * @param annotation the annotation to search for
     * @param <T>        the type of the annotation
     * @return the list of all method or type level annotations in the type hierarchy
     */
    public static <T extends Annotation> List<T> searchForAnnotation(Method method, Class<T> annotation) {
        if (method == null) {
            return Lists.newArrayList();
        }
        return searchClasses(method, annotation, method.getDeclaringClass());
    }

    private static <T extends Annotation> List<T> searchClasses(Method m, Class<T> annotation, Class<?>... classes) {
        List<T> annotations = new ArrayList<>();
        for (final Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(annotation)) {
                annotations.add(clazz.getAnnotation(annotation));
            }
            try {
                final Method iMeth = clazz.getMethod(m.getName(), m.getParameterTypes());
                if (iMeth.isAnnotationPresent(annotation)) {
                    annotations.add(iMeth.getAnnotation(annotation));
                }
            } catch (NoSuchMethodException | SecurityException e) {
                // search next
            }
            if (clazz.getInterfaces().length != 0) {
                final Class<?>[] interfaces = clazz.getInterfaces();
                annotations.addAll(JaxRsAnnotationScanner.searchClasses(m, annotation, interfaces));
            }
            if (clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Object.class)) {
                annotations.addAll(JaxRsAnnotationScanner.searchClasses(m, annotation, clazz.getSuperclass()));
            }

        }
        return annotations;
    }

}
