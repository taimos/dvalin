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

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;

import java.util.concurrent.TimeUnit;

/**
 * Copyright 2015 Hoegernet<br>
 * <br>
 * Utils for changelog creation
 *
 * @author Thorsten Hoeger
 */
public final class ChangelogUtil {

    private ChangelogUtil() {
        // private utility class constructor
    }

    /**
     * adds a TTL index to the given collection. The TTL must be a positive integer.
     *
     * @param collection the collection to use for the TTL index
     * @param field      the field to use for the TTL index
     * @param ttl        the TTL to set on the given field
     * @throws IllegalArgumentException if the TTL is less or equal 0
     */
    public static void addTTLIndex(MongoCollection collection, String field, int ttl) {
        if (ttl <= 0) {
            throw new IllegalArgumentException("TTL must be positive");
        }
        IndexOptions indexOptions = new IndexOptions();
        indexOptions.expireAfter((long) ttl, TimeUnit.SECONDS);
        collection.createIndex(new BasicDBObject(field, 1), indexOptions);
    }

    /**
     * Add an index on the given collection and field
     *
     * @param collection the collection to use for the index
     * @param field      the field to use for the index
     * @param asc        the sorting direction. <code>true</code> to sort ascending; <code>false</code> to sort descending
     * @param background iff <code>true</code> the index is created in the background
     */
    public static void addIndex(MongoCollection collection, String field, boolean asc, boolean background) {
        int dir = (asc) ? 1 : -1;
        IndexOptions indexOptions = new IndexOptions();
        indexOptions.background(background);
        collection.createIndex(new BasicDBObject(field, dir), indexOptions);
    }

    /**
     * Add a text index on the given collection
     *
     * @param collection the collection to use for the index
     */
    public static void addTextIndex(MongoCollection collection) {
        collection.createIndex(new BasicDBObject("$**", "text"));
    }

}
