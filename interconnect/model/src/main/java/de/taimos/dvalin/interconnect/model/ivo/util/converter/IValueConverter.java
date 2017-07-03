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

/**
 * @author psigloch
 */
public interface IValueConverter {

	/**
	 * @param originalFieldValue the original fields value
	 * @param target the target object
	 * @param originalField the original field
	 * @param targetField the target field
	 * @param <Destination> the type of the target object
	 * @return the converted value, which may be written to the target field, null if convertion not possible.
	 */
	<Destination> Object convert(Object originalFieldValue, Destination target, Field originalField, Field targetField);
}
