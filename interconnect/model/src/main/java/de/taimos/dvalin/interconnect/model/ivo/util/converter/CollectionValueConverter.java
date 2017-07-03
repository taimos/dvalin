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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Copyright 2017 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public class CollectionValueConverter implements IValueConverter {

	@Override
	public <Destination> Object convert(Object originalFieldValue, Destination target, Field originalField, Field targetField) {
		if(originalFieldValue instanceof Collection<?>) {
			if(((Collection) originalFieldValue).isEmpty()) {
				return originalFieldValue;
			}
			Collection<Object> list;
			if(List.class.isAssignableFrom(originalField.getType())) {
				list = new ArrayList<>();
			} else {
				list = new HashSet<>();
			}
			for(Object row : ((Collection) originalFieldValue)) {
				list.add(ConverterUtil.modifyValue(row, target, originalField, targetField));
			}
			return list;
		}
		return null;
	}
}
