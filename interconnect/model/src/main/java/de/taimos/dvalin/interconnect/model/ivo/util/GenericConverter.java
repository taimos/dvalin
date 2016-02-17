/**
 *
 */
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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.joda.time.DateTime;

import de.taimos.dvalin.interconnect.model.ivo.IVO;

public class GenericConverter {

    /**
     * @param <Destination>    the destination
     * @param <Origin>         the origin
     * @param origin           object to be copied from
     * @param destinationClass destination for reference
     * @return new object copied from origin to destination
     */
    public static <Destination, Origin> Destination convert(Origin origin, Class<Destination> destinationClass) {
        Destination result = GenericConverter.createInstance(destinationClass);
        result = GenericConverter.copy(origin, result);
        return result;
    }

    private static <Destination, Origin> Destination copy(Origin origin, Destination result) {
        HashMap<Field, Field> map = GenericConverter.resolveFieldMapFromOrigin(origin, result);
        for (Field originField : map.keySet()) {
            originField.setAccessible(true);
            Field destinationField = map.get(originField);
            // not all field can be/have to be copied
            if (destinationField != null) {
                GenericConverter.copyValue(origin, result, originField, destinationField);
            }
        }
        return result;
    }

    @SuppressWarnings({"deprecation", "rawtypes"})
    private static <Destination, Origin> void copyValue(Origin origin, Destination result, Field originField, Field destinationField) {
        try {
            Object originalValue = GenericConverter.extractValue(origin, originField);

            if (originalValue == null) {
                destinationField.set(result, null);
            } else if (originalValue instanceof Collection<?>) {
                Class<? extends Object> class1 = null;
                for (Object object : ((Collection) originalValue)) {
                    class1 = object.getClass();
                }
                if ((class1 != null) && class1.isAssignableFrom(String.class)) {
                    // TODO implement
                }
            } else if (originalValue instanceof IVO) {
                destinationField.set(result, GenericConverter.convert(originalValue, destinationField.getType()));
            } else if ((originalValue instanceof BigDecimal) && (String.class.isAssignableFrom(destinationField.getType()))) {
                destinationField.set(result, ConvertingUtils.convertBigDecimalToString((BigDecimal) originalValue));
            } else if ((originalValue instanceof String) && (BigDecimal.class.isAssignableFrom(destinationField.getType()))) {
                destinationField.set(result, ConvertingUtils.convertStringToBigDecimal((String) originalValue));
            } else if ((originalValue instanceof Date) && (DateTime.class.isAssignableFrom(destinationField.getType()))) {
                destinationField.set(result, new DateTime(originalValue));
            } else if ((originalValue instanceof DateTime) && (Date.class.isAssignableFrom(destinationField.getType()))) {
                destinationField.set(result, new DateTime(originalValue).toDate());
            } else {
                destinationField.set(result, originalValue);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    private static Object extractValue(Object object, Field field) throws IllegalAccessException {
        Object value = null;
        try {
            Method getter = GenericConverter.getGetter(object.getClass(), field.getName());
            getter.setAccessible(true);
            value = getter.invoke(object);
        } catch (NoSuchMethodException | InvocationTargetException e) {
            field.setAccessible(true);
            value = field.get(object);
        }
        return value;
    }

    private static String capitalize(String name) {
        if ((name == null) || (name.length() == 0)) {
            return name;
        }
        return name.substring(0, 1).toUpperCase(Locale.ENGLISH) + name.substring(1);
    }

    // Almost the same as in VOAnalyzer, but NoSuchMethodException is passed to the caller, logger used
    private static Method getGetter(Class<?> clazz, String fieldname) throws NoSuchMethodException {
        Method m = null;
        try {
            m = clazz.getMethod("get" + GenericConverter.capitalize(fieldname));
        } catch (SecurityException e) {
            // Logger.error("fetch of getter failed", e);
        }
        return m;
    }

    private static <Destination, Origin> HashMap<Field, Field> resolveFieldMapFromOrigin(Origin origin, Destination destination) {
        HashMap<String, Field> createFieldMap = GenericConverter.createFieldMap(origin);
        HashMap<String, Field> createFieldMap2 = GenericConverter.createFieldMap(destination);

        HashMap<Field, Field> result = new HashMap<>();
        for (Field field : createFieldMap.values()) {

            if (createFieldMap2.containsKey(field.getName())) {
                result.put(field, createFieldMap2.get(field.getName()));
            }
            if (field.getName().equals("version")) {
                result.put(field, createFieldMap2.get("_version"));
            }
            if (field.getName().equals("_version")) {
                result.put(field, createFieldMap2.get("version"));
            }
            if (field.getName().equals("lastChange")) {
                result.put(field, createFieldMap2.get("_lastChange"));
            }
            if (field.getName().equals("_lastChange")) {
                result.put(field, createFieldMap2.get("lastChange"));
            }
            if (field.getName().equals("lastChangeUserId")) {
                result.put(field, createFieldMap2.get("_lastChangeUserId"));
            }
            if (field.getName().equals("_lastChangeUserId")) {
                result.put(field, createFieldMap2.get("lastChangeUserId"));
            }
        }

        return result;
    }

    private static <Destination> Destination createInstance(Class<Destination> destinationClass) {
        Destination result = null;
        try {

            result = destinationClass.newInstance();

        } catch (InstantiationException | IllegalAccessException e) {
            // Instantiation error, failed to copy the element
            // throw new DaemonError(TMTErrors.jmsProblem);
        }
        return result;
    }

    private static HashMap<String, Field> createFieldMap(Object element) {
        HashMap<String, Field> result = new HashMap<>();
        for (Class<?> obj = element.getClass(); !obj.equals(Object.class); obj = obj.getSuperclass()) {
            for (Field field : obj.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);
                    result.put(field.getName(), field);
                }
            }
        }
        return result;
    }

}
