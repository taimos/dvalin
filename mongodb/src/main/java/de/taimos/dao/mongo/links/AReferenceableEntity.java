package de.taimos.dao.mongo.links;

/*
 * #%L
 * Spring DAO Mongo
 * %%
 * Copyright (C) 2013 - 2015 Taimos GmbH
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

import de.taimos.dao.AEntity;

/**
 * extension of {@link AEntity} which is referenceable via DocumentLink
 * 
 * @author Thorsten Hoeger
 *
 * @param <T> generic link to myself
 */
public abstract class AReferenceableEntity<T extends AReferenceableEntity<T>> extends AEntity {
	
	private static final long serialVersionUID = 1L;
	
	
	@JsonIgnore
	@SuppressWarnings("unchecked")
	public DocumentLink<T> asLink() {
		return new DocumentLink<T>((T) this);
	}
	
	@JsonIgnore
	protected abstract String getLabel();
	
}
