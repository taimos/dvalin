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
public class MapValueConverter implements IValueConverter {
    @Override
    public <Destination> Object convert(Object originalFieldValue, Destination target, Field originalField, Field targetField) {
        if(originalFieldValue instanceof Map) {
            if(((Map) originalFieldValue).isEmpty()) {
                return originalFieldValue;
            }
            Map<Object, Object> map = new HashMap<>();
            for(Map.Entry<Object, Object> entry : ((Map<Object, Object>) originalFieldValue).entrySet()) {
                map.put(entry.getKey(), ConverterUtil.modifyValue(entry.getValue(), target, originalField, targetField));
            }
            return map;
        }
        return null;
    }
}
