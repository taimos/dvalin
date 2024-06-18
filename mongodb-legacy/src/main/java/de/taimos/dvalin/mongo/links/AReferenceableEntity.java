package de.taimos.dvalin.mongo.links;

/*-
 * #%L
 * MongoDB support for dvalin
 * %%
 * Copyright (C) 2015 - 2018 Taimos GmbH
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

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * referenceable via DocumentLink
 *
 * @param <T> generic link to myself
 * @author Thorsten Hoeger
 */
public interface AReferenceableEntity<T extends AReferenceableEntity<T>> {

    @JsonIgnore
    @SuppressWarnings("unchecked")
    default DocumentLink<T> asLink() {
        return new DocumentLink<>((T) this);
    }

    @JsonIgnore
    String getLabel();

    String getId();

}
