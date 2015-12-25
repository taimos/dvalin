package de.taimos.dvalin.mongo;

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

import org.jongo.ResultHandler;

import com.mongodb.DBObject;

/**
 * Copyright 2015 Hoegernet<br>
 * <br>
 * abstract {@link ResultHandler} used in map-reduce-operations in the DAO classes
 *
 * @param <T> the type of the MR result objects
 * @author Thorsten Hoeger
 */
public abstract class MapReduceResultHandler<T> implements ResultHandler<T> {

    @Override
    public T map(DBObject result) {
        return this.map(result.get("_id"), result.get("value"));
    }

    /**
     * map the given map-reduce result to the desired class
     *
     * @param key   the key of the map function
     * @param value the value of the reduce function for the given key
     * @return the converted element
     */
    protected abstract T map(Object key, Object value);

}
