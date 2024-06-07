package de.taimos.dvalin.mongo;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import org.bson.BsonDocument;
import org.bson.BsonObjectId;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

/**
 * @param <T> the entity this DAO is used for
 * @author Thorsten Hoeger
 */
public abstract class AbstractAuditedMongoDAO<T extends AAuditedEntity> extends AbstractMongoDAO<T>
    implements ICrudAuditedDAO<T> {

    private static final String HISTORY_OBJECT_ID = "originalId";

    private MongoCollection<Document> historyCollection;
    private ObjectMapper mapper;

    @Override
    protected void customInit() {
        this.mapper = this.dataAccess.getMapper();
        String collectionName = this.dataAccess.getCollectionName() + "_history";
        this.historyCollection = this.dataAccess.getDb().getCollection(collectionName);
    }

    @Override
    public T findVersion(String id, Integer version) {
        if (version == null) {
            return this.findById(id);
        }

        return this.historyCollection.find(and(eq(AbstractAuditedMongoDAO.getIdFilter(id)), eq("version", version)))
            .map(this::mapToEntity).first();
    }

    @Override
    public List<T> findHistoryElements(String id) {
        return this.historyCollection.find(AbstractAuditedMongoDAO.getIdFilter(id)).sort(Sorts.descending("version"))
            .map(this::mapToEntity).into(new ArrayList<>());
    }

    private T mapToEntity(Document document) {
        return this.dataAccess.mapToEntity(document);
    }

    private static Bson getIdFilter(String id) {
        return eq(AbstractAuditedMongoDAO.HISTORY_OBJECT_ID, new ObjectId(id));
    }

    @Override
    protected void beforeSave(T object) {
        Integer oldVersion = object.getVersion();
        if (oldVersion == null) {
            object.setVersion(0);
        } else {
            object.setVersion(oldVersion + 1);
        }
        object.setLastChange(DateTime.now());
    }

    @Override
    protected void afterSave(T object) {
        try {
            String json = this.mapper.writeValueAsString(object);
            BsonDocument bsonDocument = BsonDocument.parse(json);
            bsonDocument.remove(this.dataAccess.idField());
            bsonDocument.append(AbstractAuditedMongoDAO.HISTORY_OBJECT_ID,
                new BsonObjectId(new ObjectId(object.getId())));

            this.historyCollection.insertOne(this.bsonToDocument(bsonDocument));
        } catch (Exception e) {
            String message = String.format("Unable to save object %s due to a marshalling error", object);
            throw new IllegalArgumentException(message, e);
        }
    }

}
