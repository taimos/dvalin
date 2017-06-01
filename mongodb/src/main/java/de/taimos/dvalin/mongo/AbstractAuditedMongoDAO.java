package de.taimos.dvalin.mongo;

import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.jongo.Jongo;
import org.jongo.Mapper;
import org.jongo.MongoCollection;
import org.jongo.bson.BsonDocument;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoDatabase;

public abstract class AbstractAuditedMongoDAO<T extends AAuditedEntity> extends AbstractMongoDAO<T> implements ICrudAuditedDAO<T> {
    
    protected MongoCollection jongoHistoryCollection;
    private com.mongodb.client.MongoCollection historyCollection;
    private Mapper jongoMapper;
    
    @Override
    protected void customInit(MongoDatabase db, Jongo jongo) {
        String collectionName = this.getCollectionName() + "_history";
        this.jongoHistoryCollection = jongo.getCollection(collectionName);
        this.historyCollection = db.getCollection(collectionName);
        this.jongoMapper = jongo.getMapper();
    }
    
    @Override
    public List<T> findHistoryElements(String id) {
        Iterable<T> as = this.jongoHistoryCollection.find("{originalId : #}", new ObjectId(id)).sort("{version: -1}").as(this.getEntityClass());
        return this.convertIterable(as);
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
