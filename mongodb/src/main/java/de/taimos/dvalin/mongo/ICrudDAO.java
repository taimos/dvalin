package de.taimos.dvalin.mongo;

/*
 * #%L
 * Spring DAO Mongo
 * %%
 * Copyright (C) 2013 Taimos GmbH
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
 * Copyright 2015 Hoegernet<br>
 * <br>
 * DAO with default CRUD operations
 *
 * @param <T> type of the managed {@link AEntity}
 * @author Thorsten Hoeger
 */
public interface ICrudDAO<T extends AEntity> {

    /**
     * find the element with the given id
     *
     * @param id the id to find
     * @return the obejct with the given id or <code>null</code> if no element exists with this id
     */
    T findById(String id);

    /**
     * saves the given element
     *
     * @param object the element to save
     * @return the saved element
     */
    T save(T object);

    /**
     * deletes the given object using its id.
     *
     * @param object the object to delete
     */
    void delete(T object);

    /**
     * deletes the given object using its id.
     *
     * @param id the id of the object to delete
     */
    void delete(String id);

    /**
     * @return a list of all elements in this collection
     */
    List<T> findList();
    
    /**
     * @param sortProp the sort parameter
     * @param sortDirection the sort direction
     * @param limit the limit
     * @param skip the number of documents to skip
     * @return list of sorted stored contracts
     */
    List<T> findList(String sortProp, Integer sortDirection, Integer limit, Integer skip);
    
    
}
