package de.taimos.dvalin.mongo;

/*-
 * #%L
 * MongoDB support for dvalin
 * %%
 * Copyright (C) 2015 - 2018 Taimos GmbH
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.Function;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.model.Sorts;
import de.taimos.dvalin.daemon.spring.InjectionUtils;
import de.taimos.dvalin.mongo.id.IdEntity;
import de.taimos.dvalin.mongo.mapper.JacksonConfig;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

/**
 * @param <T> document class for which this will be the DB access
 * @author fzwirn
 */
@Repository
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@SuppressWarnings("unused")
public class MongoDBDataAccess<T extends IdEntity> {

    /**
     * Query to perform a full text search
     */
    public static final String FULLTEXT_QUERY = "{\"$text\" : {\"$search\" : #}}";
    public static final String DEFAULT_ID = "_id";

    private final MongoDatabase db;
    private final Class<T> entityClass;
    private final MongoCollection<Document> collection;
    private ObjectMapper mapper;

    /**
     * @param db database
     * @param ip injection point
     */
    @Autowired
    public MongoDBDataAccess(MongoDatabase db, InjectionPoint ip) {
        this.db = db;
        //noinspection unchecked
        this.entityClass = (Class<T>) InjectionUtils.getGenericType(ip);
        this.collection = db.getCollection(this.getCollectionName());
        this.initMapper();
    }

    protected void initMapper() {
        this.mapper = JacksonConfig.createObjectMapper();
    }

    /**
     * @return the mapper
     */
    public ObjectMapper getMapper() {
        return this.mapper;
    }

    /**
     * @param mapper the mapper to set
     */
    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * @return the id Field used by the entity
     */
    public String idField() {
        return MongoDBDataAccess.DEFAULT_ID;
    }

    /**
     * @return the name of the collection
     */
    public String getCollectionName() {
        return this.entityClass.getSimpleName();
    }

    /**
     * @return the class of the entity
     */
    public Class<T> getEntityClass() {
        return this.entityClass;
    }

    /**
     * @return all elements sorted by id field
     */
    public final List<T> findList() {
        return this.collection.find().sort(Sorts.ascending(this.idField())).map(this::mapToEntity)
            .into(new ArrayList<>());
    }

    /**
     * @param sortProp      property to sort by
     * @param sortDirection direction to sort by
     * @param skip          the number of elements to skip
     * @param limit         the number of elements to fetch
     * @return the list of elements found
     */
    public List<T> findList(String sortProp, Integer sortDirection, Integer limit, Integer skip) {
        return this.collection.find().sort(sortDirection == -1 ? Sorts.descending(sortProp) : Sorts.ascending(sortProp))
            .skip(skip).limit(limit).map(this::mapToEntity).into(new ArrayList<>());
    }

    /**
     * finds all elements matching the given query and sorts them accordingly. With this method it is possible to specify a projection to
     * rename or filter fields in the result elements.
     *
     * @param query      the query to search for
     * @param sort       the sort query to apply
     * @param skip       the number of elements to skip
     * @param limit      the number of elements to fetch
     * @param projection the projection of fields to use
     * @return the list of elements found
     */
    public final List<T> findSortedByQuery(Bson query, Bson sort, Integer skip, Integer limit, Bson projection) {
        return this.innerFindSortedByQuery(query, sort, skip, limit, projection).map(this::mapToEntity)
            .into(new ArrayList<>());
    }

    private FindIterable<Document> innerFindSortedByQuery(Bson query, Bson sort, Integer skip, Integer limit, Bson projection) {
        FindIterable<Document> result = this.collection.find(query).sort(sort).limit(limit != null ? limit : 0)
            .projection(projection);
        if (skip != null) {
            result = result.skip(skip);
        }
        return result;
    }


    /**
     * finds all elements matching the given query and sorts them accordingly. With this method it is possible to specify a projection to
     * rename or filter fields in the result elements. Instead of returning objects it returns objects converted
     * by the given {@link Function}
     *
     * @param query           the query to search for
     * @param sort            the sort query to apply
     * @param skip            the number of elements to skip
     * @param limit           the number of elements to fetch
     * @param projection      the projection of fields to use
     * @param mappingFunction the handler to convert result elements with
     * @param <R>             the element type
     * @return the list of elements found
     */
    public final <R> List<R> findSortedByQuery(Bson query, Bson sort, Integer skip, Integer limit, Bson projection, Function<T, R> mappingFunction) {
        return this.innerFindSortedByQuery(query, sort, skip, limit, projection).map(this::mapToEntity)
            .map(mappingFunction).into(new ArrayList<>());
    }

    /**
     * finds all elements containing the given searchString in any text field and sorts them accordingly.
     *
     * @param searchString the searchString to search for
     * @param sort         the sort query to apply
     * @return the list of elements found
     */
    public final List<T> searchSorted(String searchString, Bson sort) {
        return this.findSortedByQuery(MongoDBDataAccess.createQuery(MongoDBDataAccess.FULLTEXT_QUERY, searchString),
            sort, null, null, null);
    }


    /**
     * queries with the given query, sorts the result and returns the first element. Empty Optional is returned if no element is found.
     *
     * @param query the query
     * @param sort  the sort
     * @return optional of the first element found or <code>null</code> if none is found
     */
    public final Optional<T> findFirstByQuery(Bson query, Bson sort) {
        return Optional.ofNullable(this.collection.find(query).sort(sort).map(this::mapToEntity).first());
    }

    /**
     * @param id of the object
     * @return optional with the object or empty optional
     */
    public final Optional<T> findById(String id) {
        return Optional.ofNullable(
            this.collection.find(MongoDBDataAccess.getIdFilter(id)).map(this::mapToEntity).first());
    }

    private static Bson getIdFilter(String id) {
        return eq(new ObjectId(id));
    }

    /**
     * @param id the id
     * @deprecated use findById - this one will be deleted with next version
     */
    @Deprecated
    public final Optional<T> findByObjectId(String id) {
        return this.findById(id);
    }

    /**
     * @param id the id
     * @deprecated use findById - this one will be deleted with next version
     */
    @Deprecated
    public final Optional<T> findByStringId(String id) {
        return this.findById(id);
    }

    /**
     * @param query the query as Bson
     * @return the number of elements matching the query
     */
    public final long count(Bson query) {
        return this.collection.countDocuments(query);
    }

    private static Bson createQuery(String query, Object... params) {
        String filledQuery = query;

        for (Object param : params) {
            if (filledQuery.indexOf('#') != -1) {
                filledQuery = filledQuery.replaceFirst("#", "\\\"" + param.toString() + "\\\"");
            }
        }

        return BsonDocument.parse(filledQuery);
    }


    protected T mapToEntity(Document document) {
        try {
            return this.mapper.readValue(document.toJson(), this.entityClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param object to save
     * @return saved object
     */
    public final T save(T object) {
        try {
            Document document = Document.parse(this.mapper.writeValueAsString(object));
            if (this.collection.countDocuments(MongoDBDataAccess.getIdFilter(object.getId())) == 0) {
                this.collection.insertOne(document);
            } else {
                this.collection.replaceOne(MongoDBDataAccess.getIdFilter(object.getId()), document);
            }
            return object;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param id of the object, that will be deleted
     */
    public final void deleteById(String id) {
        this.collection.deleteOne(MongoDBDataAccess.getIdFilter(id));
    }

    /**
     * @param id the di
     * @deprecated use deleteById - this one will be deleted with next version
     */
    @Deprecated
    public final void deleteByObjectId(String id) {
        this.deleteById(id);
    }

    /**
     * @param id the di
     * @deprecated use deleteById - this one will be deleted with next version
     */
    @Deprecated
    public final void deleteByStringId(String id) {
        this.deleteById(id);
    }

    /**
     * @param query to select the objects that will be deleted
     */
    public final void delete(String query) {
        this.collection.deleteOne(BsonDocument.parse(query));
    }

    /**
     * @param id     of the object
     * @param update list of updates to perfom
     */
    public final void update(ObjectId id, List<? extends Bson> update) {
        this.collection.updateOne(eq(id), update);
    }

    /**
     * @param query  to select the objects that will be updated
     * @param update list of updates to perfom
     */
    public final void update(String query, List<? extends Bson> update) {
        this.collection.updateOne(BsonDocument.parse(query), update);
    }

    /**
     * @return the MongoCollection
     */
    public MongoCollection<Document> getCollection() {
        return this.collection;
    }

    /**
     * @return the db
     */
    public MongoDatabase getDb() {
        return this.db;
    }

    /**
     * @param bucket name of the bucket
     * @return the GridFS bucket
     */
    public GridFSBucket getGridFSBucket(String bucket) {
        if (bucket == null || bucket.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return GridFSBuckets.create(this.db, bucket);
    }

}
