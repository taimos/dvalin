package de.taimos.dao;

/*
 * #%L
 * Hibernate DAO for Spring
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

import java.util.List;

/**
 * @author hoegertn
 * 
 * @param <E>
 *            the entity type
 * @param <I>
 *            the id type
 */
public interface IEntityDAO<E extends IEntity<I>, I> {
	
	/**
	 * @param element
	 * @return the saved element
	 */
	E save(E element);
	
	/**
	 * @param element
	 */
	void delete(E element);
	
	/**
	 * @param id
	 */
	void deleteById(I id);
	
	/**
	 * @param id
	 * @return the element with the given id or null if not found
	 */
	E findById(I id);
	
	/**
	 * @return the list of elements
	 */
	List<E> findList();
	
	/**
	 * @param first
	 *            first result
	 * @param max
	 *            max results
	 * @return the list of elements
	 */
	List<E> findList(int first, int max);
	
	/**
	 * @return the entity class
	 */
	Class<E> getEntityClass();
	
}
