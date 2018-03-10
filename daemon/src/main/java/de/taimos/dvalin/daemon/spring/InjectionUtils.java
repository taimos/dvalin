package de.taimos.dvalin.daemon.spring;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.core.ResolvableType;

public final class InjectionUtils {

    private InjectionUtils() {
        //
    }

    public static Class<?> getGenericType(InjectionPoint ip) {
        if (ip.getField() != null) {
            return resolve(ip.getField().getGenericType(), ip);
        }
        if (ip.getMethodParameter() != null) {
            return resolve(ip.getMethodParameter().getGenericParameterType(), ip);
        }
        throw new IllegalArgumentException("Cannot derive generic type from InjectionPoint");
    }

    private static Class<?> resolve(Type genericType, InjectionPoint ip) {
        if (genericType instanceof ParameterizedType) {
            //noinspection unchecked
            Type type = ((ParameterizedType) genericType).getActualTypeArguments()[0];
            if (type instanceof Class) {
                return (Class) type;
            }
            if (type instanceof TypeVariable && ip instanceof DependencyDescriptor) {
                ResolvableType owner = ((DependencyDescriptor) ip).getResolvableType();
                ResolvableType resolvableType = ResolvableType.forType(type, owner);
                return resolvableType.resolve();
            }
        }
        throw new IllegalArgumentException("Cannot derive type from InjectionPoint member");
    }

    public static DependencyDescriptor createDependencyDescriptor(Field field, Object container) {
        DependencyDescriptor dd = new DependencyDescriptor(field, true);
        dd.setContainingClass(container.getClass());
        return dd;
    }

}
