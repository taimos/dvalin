package de.taimos.dvalin.jaxrs;

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
