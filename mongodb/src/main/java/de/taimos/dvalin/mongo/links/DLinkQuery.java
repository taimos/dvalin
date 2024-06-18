package de.taimos.dvalin.mongo.links;

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

import com.mongodb.DBObject;
import com.mongodb.client.model.Projections;
import de.taimos.dvalin.mongo.MongoDBDataAccess;
import org.bson.conversions.Bson;

import java.util.List;

/**
 * QueryHelper to convert query result to a list of DLinks. It only queries the fields necessary to construct links.
 *
 * @param <T> the {@link AReferenceableEntity} this link uses
 * @author Thorsten Hoeger
 */
public class DLinkQuery<T extends AReferenceableEntity<T>> {

    private final Class<T> targetClass;

    private final String labelField;


    /**
     * @param targetClass the links target class
     * @param labelField  the name of the label field
     */
    public DLinkQuery(Class<T> targetClass, String labelField) {
        this.targetClass = targetClass;
        this.labelField = labelField;
    }

    public List<DocumentLink<T>> find(MongoDBDataAccess<?> dataAccess, Bson query) {
        return dataAccess.findSortedByQuery(query, null, null, null, Projections.include(this.labelField), this::convert);
    }

    private DocumentLink<T> convert(Object result) {
        if (!(result instanceof DBObject)) {
            throw new RuntimeException("Wront response for DocumentLink");
        }
        if (!((DBObject) result).containsField("_id") ||
            !((DBObject) result).containsField(DLinkQuery.this.labelField)) {
            throw new RuntimeException("Fields missing to construct DocumentLink");
        }
        String id = ((DBObject) result).get("_id").toString();
        String label = ((DBObject) result).get(DLinkQuery.this.labelField).toString();
        return new DocumentLink<>(DLinkQuery.this.targetClass, id, label);
    }

}
