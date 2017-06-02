package org.springframework.data.mongodb.core;

/*-
 * #%L
 * MongoDB support for dvalin
 * %%
 * Copyright (C) 2015 - 2017 Taimos GmbH
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

import com.mongodb.Mongo;

/**
 * Overriding MongoTemplate from Spring Data because Mongobee has class binding at startup fails without this class.
 * Dvalin does not use Spring Data so this class is not available.
 */
public class MongoTemplate {
    
    private final Mongo mongo;
    private final String dbName;
    
    public MongoTemplate(Mongo mongo, String dbName) {
        this.mongo = mongo;
        this.dbName = dbName;
    }
    
    public Mongo getMongo() {
        return this.mongo;
    }
    
    public String getDbName() {
        return this.dbName;
    }
}
