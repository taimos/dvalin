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

import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.jongo.Jongo;
import org.jongo.Mapper;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;
import org.jongo.bson.BsonDocument;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoDatabase;

public abstract class AbstractAuditedMongoDAO<T extends AAuditedEntity> extends AbstractMongoDAO<T> implements ICrudAuditedDAO<T> {

    protected MongoCollection jongoHistoryCollection;
    private com.mongodb.client.MongoCollection historyCollection;
    private Mapper jongoMapper;

    @Override
    protected void customInit(MongoDatabase db, Jongo jongo) {
        String collectionName = this.dataAccess.getCollectionName() + "_history";
        this.jongoHistoryCollection = jongo.getCollection(collectionName);
        this.historyCollection = db.getCollection(collectionName);
        this.jongoMapper = jongo.getMapper();
    }

    @Override
    public T findVersion(String id, Integer version) {
        if (version == null) {
            return this.findById(id);
        }

        MongoCursor<T> as = this.jongoHistoryCollection.find("{originalId:#, version:#}", new ObjectId(id), version).as(this.dataAccess.getEntityClass());
        if (as.hasNext()) {
            return as.next();
        }
        return null;
    }

    @Override
    public List<T> findHistoryElements(String id) {
        Iterable<T> as = this.jongoHistoryCollection.find("{originalId : #}", new ObjectId(id)).sort("{version: -1}").as(this.dataAccess.getEntityClass());
        return this.dataAccess.convertIterable(as);
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
            BsonDocument bsonDocument = this.jongoMapper.getMarshaller().marshall(object);
            BasicDBObject dbObject = new BasicDBObject(bsonDocument.toDBObject().toMap());
            dbObject.removeField("_id");
            dbObject.put("originalId", new ObjectId(object.getId()));
            Document doc = Document.parse(dbObject.toString());
            this.historyCollection.insertOne(doc);
        } catch (Exception e) {
            String message = String.format("Unable to save object %s due to a marshalling error", object);
            throw new IllegalArgumentException(message, e);
        }
    }
}
