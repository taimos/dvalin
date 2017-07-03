package de.taimos.dvalin.interconnect.model.ivo.util.converter;
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
import java.util.HashMap;
import java.util.Map;

/**
 * @author psigloch
 */
public class ConverterUtil {

    private static final Map<String, IValueConverter> valueConverters = new HashMap<>();

    static {
        ConverterUtil.addValueConverter(new CollectionValueConverter());
        ConverterUtil.addValueConverter(new MapValueConverter());
        ConverterUtil.addValueConverter(new PrimitiveValueConverter());
        ConverterUtil.addValueConverter(new MongoObjValueConverter());
        ConverterUtil.addValueConverter(new IVOValueConverter());
    }

    /**
     * @param modifier add an special value modifier
     */
    public static void addValueConverter(IValueConverter modifier) {
        ConverterUtil.valueConverters.put(modifier.getClass().getSimpleName(), modifier);
    }

    /**
     * @param originalFieldValue the original field value
     * @param target             the target class
     * @param originalField      the original field
     * @param targetField        the target field
     * @param <Destination>      the destination class
     * @return the new field value
     */
    public static <Destination> Object modifyValue(Object originalFieldValue, Destination target, Field originalField, Field targetField) {
        if(originalFieldValue == null) {
            return null;
        }
        for(IValueConverter vm : ConverterUtil.valueConverters.values()) {
            Object result = vm.convert(originalFieldValue, target, originalField, targetField);
            if(result != null) {
                return result;
            }
        }
        return originalFieldValue;
    }
}
