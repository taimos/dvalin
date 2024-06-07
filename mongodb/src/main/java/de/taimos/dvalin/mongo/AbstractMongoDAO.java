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

import com.mongodb.Function;
import com.mongodb.client.gridfs.GridFSBucket;
import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.Document;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.DocumentCodec;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Copyright 2015 Hoegernet<br>
 * <br>
 * abstract class to derive to implement Mongo DAOs. The system property <code>mongodb.name</code> has to be set and it denotes the database
 * used for this DAO.
 *
 * @param <T> the entity this DAO is used for
 * @author Thorsten Hoeger
 */
public abstract class AbstractMongoDAO<T extends AEntity> implements ICrudDAO<T> {

    protected DocumentCodec codec;
    protected DecoderContext decoderContext;

    @Autowired
    protected MongoDBDataAccess<T> dataAccess;

    @PostConstruct
    public final void init() {
        this.customInit();
        this.codecInit();
    }

    protected void codecInit() {
        // override if needed
        this.codec = new DocumentCodec();
        this.decoderContext = DecoderContext.builder().build();
    }

    protected void customInit() {
        // implement if needed
    }

    @Override
    public final List<T> findList() {
        return this.dataAccess.findList();
    }

    @Override
    public List<T> findList(String sortProp, Integer sortDirection, Integer limit, Integer skip) {
        return this.dataAccess.findList(sortProp, sortDirection, limit, skip);
    }

    /**
     * finds all elements matching the given query
     *
     * @param query the query to search for
     * @return the list of elements found
     */
    protected final List<T> findByQuery(Bson query) {
        return this.dataAccess.findSortedByQuery(query, null, null, null, null);
    }

    /**
     * finds all elements matching the given query and sorts them accordingly
     *
     * @param query the query to search for
     * @param sort  the sort query to apply
     * @return the list of elements found
     */
    protected final List<T> findSortedByQuery(Bson query, Bson sort) {
        return this.dataAccess.findSortedByQuery(query, sort, null, null, null);
    }

    /**
     * finds all elements matching the given query and sorts them accordingly. With this method it is possible to specify a projection to
     * rename or filter fields in the result elements. Instead of returning typed objects it returns objects of type
     * <code>as</code>
     *
     * @param query      the query to search for
     * @param sort       the sort query to apply
     * @param projection the projection of fields to use
     * @return the list of elements found
     */
    protected final List<T> findSortedByQuery(Bson query, Bson sort, Bson projection) {
        return this.dataAccess.findSortedByQuery(query, sort, null, null, projection);
    }

    /**
     * finds all elements matching the given query and sorts them accordingly. With this method it is possible to specify a projection to
     * rename or filter fields in the result elements. Instead of returning typed objects it returns objects converted
     * by the given {@link Function}
     *
     * @param query      the query to search for
     * @param sort       the sort query to apply
     * @param projection the projection of fields to use
     * @param handler    the handler to convert result elements with
     * @param <P>        the element type
     * @return the list of elements found
     */
    protected final <P> List<P> findSortedByQuery(Bson query, Bson sort, Bson projection, Function<T, P> handler) {
        return this.dataAccess.findSortedByQuery(query, sort, null, null, projection, handler);
    }

    /**
     * finds all elements matching the given query and sorts them accordingly
     *
     * @param query the query to search for
     * @param sort  the sort query to apply
     * @param skip  the number of elements to skip
     * @param limit the number of elements to fetch
     * @return the list of elements found
     */
    protected final List<T> findSortedByQuery(Bson query, Bson sort, Integer skip, Integer limit) {
        return this.dataAccess.findSortedByQuery(query, sort, skip, limit, null);
    }

    /**
     * finds all elements matching the given query and sorts them accordingly. With this method it is possible to specify a projection to
     * rename or filter fields in the result elements. Instead of returning typed objects it returns objects of type
     * <code>as</code>
     *
     * @param query      the query to search for
     * @param sort       the sort query to apply
     * @param skip       the number of elements to skip
     * @param limit      the number of elements to fetch
     * @param projection the projection of fields to use
     * @return the list of elements found
     */
    protected final List<T> findSortedByQuery(Bson query, Bson sort, Integer skip, Integer limit, Bson projection) {
        return this.dataAccess.findSortedByQuery(query, sort, skip, limit, projection);
    }

    /**
     * finds all elements matching the given query and sorts them accordingly. With this method it is possible to specify a projection to
     * rename or filter fields in the result elements. Instead of returning typed objects it returns objects converted
     * by the given {@link Function}
     *
     * @param query      the query to search for
     * @param sort       the sort query to apply
     * @param skip       the number of elements to skip
     * @param limit      the number of elements to fetch
     * @param projection the projection of fields to use
     * @param handler    the handler to convert result elements with
     * @param <R>        the element type
     * @return the list of elements found
     */
    protected final <R> List<R> findSortedByQuery(Bson query, Bson sort, Integer skip, Integer limit, Bson projection, Function<T, R> handler) {
        return this.dataAccess.findSortedByQuery(query, sort, skip, limit, projection, handler);
    }

    /**
     * finds all elements containing the given searchString in any text field and sorts them accordingly.
     *
     * @param searchString the searchString to search for
     * @param sort         the sort query to apply
     * @return the list of elements found
     */
    protected final List<T> searchSorted(String searchString, Bson sort) {
        return this.dataAccess.searchSorted(searchString, sort);
    }

    /**
     * queries with the given string, sorts the result and returns the first element. <code>null</code> is returned if no element is found.
     *
     * @param query  the query
     * @param sort   the sort
     * @return the first element found or <code>null</code> if none is found
     */
    protected final T findFirstByQuery(Bson query, Bson sort) {
        return this.dataAccess.findFirstByQuery(query, sort).orElse(null);
    }

    @Override
    public final T findById(String id) {
        return this.dataAccess.findById(id).orElse(null);
    }

    @Override
    public final T save(T object) {
        this.beforeSave(object);
        T saved = this.dataAccess.save(object);
        this.afterSave(saved);
        return saved;
    }

    /**
     * override this to do something after the object was saved
     *
     * @param object the saved object
     */
    @SuppressWarnings("unused")
    protected void afterSave(T object) {
        //
    }

    /**
     * override this to do something before the object was saved
     *
     * @param object the object to be saved
     */
    @SuppressWarnings("unused")
    protected void beforeSave(T object) {
        //
    }

    @Override
    public final void delete(T object) {
        this.delete(object.getId());
    }

    @Override
    public final void delete(String id) {
        this.beforeDelete(id);
        this.dataAccess.deleteById(id);
        this.afterDelete(id);
    }

    @SuppressWarnings("unused")
    protected void beforeDelete(String id) {
        //
    }

    @SuppressWarnings("unused")
    protected void afterDelete(String id) {
        //
    }

    protected GridFSBucket getGridFSBucket(String bucket) {
        return this.dataAccess.getGridFSBucket(bucket);
    }

    protected Document bsonToDocument(BsonDocument bsonDocument) {
        return this.codec.decode(new BsonDocumentReader(bsonDocument), this.decoderContext);
    }
}
