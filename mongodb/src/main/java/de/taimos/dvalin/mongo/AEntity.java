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

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.jongo.marshall.jackson.oid.MongoId;
import org.jongo.marshall.jackson.oid.MongoObjectId;

/**
 * Copyright 2015 Hoegernet<br>
 * <br>
 * superclass to implement for Elements to be stored into the MongoDB. It provides an _id field which is pre-filled with a new
 * {@link ObjectId}
 *
 * @author Thorsten Hoeger
 */
public abstract class AEntity implements Serializable {

    private static final long serialVersionUID = 6328501276339927785L;

    @MongoId
    @MongoObjectId
    protected String id = ObjectId.get().toString();


    /**
     * @return the unique id of the element
     */
    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

}
