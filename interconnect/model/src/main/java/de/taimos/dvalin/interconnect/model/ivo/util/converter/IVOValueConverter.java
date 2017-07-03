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

import de.taimos.dvalin.interconnect.model.ivo.IVO;
import de.taimos.dvalin.interconnect.model.ivo.util.GenericConverter;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.WildcardType;
import java.util.Collection;

/**
 * @author psigloch
 */
public class IVOValueConverter implements IValueConverter {

    @Override
    public <Destination> Object convert(Object originalFieldValue, Destination target, Field originalField, Field targetField) {
        if(originalFieldValue instanceof IVO) {
            Class<?> type = targetField.getType();
            if(Collection.class.isAssignableFrom(type)) {
                ParameterizedType stringListType = (ParameterizedType) targetField.getGenericType();
                try {
                    type = (Class<?>) ((WildcardType) (stringListType.getActualTypeArguments()[0])).getUpperBounds()[0];
                } catch(ClassCastException e) {
                    type = (Class<?>) (stringListType.getActualTypeArguments()[0]);
                }
            }
            if(type.isInterface()) {
                type = originalFieldValue.getClass();
            }
            return GenericConverter.convert(originalFieldValue, type);
        }
        return null;
    }
}
