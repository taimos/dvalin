package de.taimos.dvalin.interconnect.model.ivo.util;

/*
 * #%L
 * Dvalin interconnect transfer data model
 * %%
 * Copyright (C) 2016 Taimos GmbH
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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.taimos.dvalin.interconnect.model.ivo.util.converter.ConverterUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;

/**
 * @author psigloch
 */
public class GenericConverter {

    /**
     * @param <Destination>    the destination
     * @param <Origin>         the origin
     * @param origin           object to be copied from
     * @param destinationClass destination for reference
     * @return new object copied from origin to destination
     */
    @SuppressWarnings("unchecked")
    public static <Destination, Origin> Destination convert(Origin origin, Class<Destination> destinationClass) {
        Class targetClazz = destinationClass;
        boolean usesBuilder = false;

        //if there is a builder present, use it
        JsonDeserialize annotation = destinationClass.getAnnotation(JsonDeserialize.class);
        if(annotation != null) {
            targetClazz = annotation.builder();
            usesBuilder = true;
        }
        Object result = GenericConverter.createNewClassInstance(targetClazz);
        result = GenericConverter.copy(origin, result);
        if(usesBuilder) {
            return GenericConverter.invokeBuilderMethod(result);
        }
        return (Destination) result;
    }

    private static <Destination, Origin> Destination copy(Origin origin, Destination target) {
        HashMap<Field, Field> map = GenericConverter.prepareFieldMapping(origin, target);
        for(Entry<Field, Field> entry : map.entrySet()) {
            //we only want fields which are present in both objects
            if(entry.getValue() != null && entry.getKey() != null) {
                try {
                    entry.getKey().setAccessible(true);
                    Object originalFieldValue = GenericConverter.getFieldValue(origin, entry.getKey());
                    entry.getValue().set(target, ConverterUtil.modifyValue(originalFieldValue, target, entry.getKey(), entry.getValue()));
                } catch(IllegalAccessException e) {
                    throw new RuntimeException("Failed to access field:" + entry.getKey().getName(), e);
                }
            }
        }
        return target;
    }


    private static Object getFieldValue(Object object, Field field) throws IllegalAccessException {
        try {
            String capitalizedFieldName = field.getName();
            if((capitalizedFieldName != null) && (!capitalizedFieldName.isEmpty())) {
                capitalizedFieldName = capitalizedFieldName.substring(0, 1).toUpperCase(Locale.ENGLISH) + capitalizedFieldName.substring(1);
            }
            Method getter = object.getClass().getMethod("get" + capitalizedFieldName);
            getter.setAccessible(true);
            return getter.invoke(object);
        } catch(NoSuchMethodException | InvocationTargetException e) {
            //since we don't know if there is a getter which does additional work, this is only fallback behaviour
            field.setAccessible(true);
            return field.get(object);
        }
    }

    @SuppressWarnings("unchecked")
    private static <Destination> Destination invokeBuilderMethod(Object result) {
        try {
            Method method = result.getClass().getMethod("build");
            return (Destination) method.invoke(result);
        } catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("No build method found");
        }
    }

    private static Object createNewClassInstance(Class destinationClass) {
        try {
            return destinationClass.newInstance();
        } catch(InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Failed to instanciate the target class:" + destinationClass.getName(), e);
        }
    }

    private static <Destination, Origin> HashMap<Field, Field> prepareFieldMapping(Origin origin, Destination destination) {
        HashMap<String, Field> createFieldMap = GenericConverter.createFieldMap(origin);
        HashMap<String, Field> createFieldMap2 = GenericConverter.createFieldMap(destination);

        HashMap<Field, Field> result = new HashMap<>();
        for(Field field : createFieldMap.values()) {
            if(createFieldMap2.containsKey(field.getName())) {
                result.put(field, createFieldMap2.get(field.getName()));
            }
        }
        return result;
    }

    private static HashMap<String, Field> createFieldMap(Object element) {
        HashMap<String, Field> result = new HashMap<>();
        //we need all fields, even those from the superclasses
        for(Class<?> clazz = element.getClass(); !clazz.equals(Object.class); clazz = clazz.getSuperclass()) {
            for(Field field : clazz.getDeclaredFields()) {
                //but we skip static fields
                if(!Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);
                    result.put(field.getName(), field);
                }
            }
        }
        return result;
    }
}
