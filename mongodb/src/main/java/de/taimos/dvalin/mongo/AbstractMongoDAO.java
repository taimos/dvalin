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

import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;

import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.ResultHandler;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;

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

    @Autowired
    @Deprecated
    protected MongoClient mongo;

    @Autowired
    private Jongo jongo;
    @Autowired
    private MongoDatabase db;

    @Deprecated
    protected MongoCollection collection;

    @Autowired
    protected MongoDBDataAccess<T> dataAccess;

    @PostConstruct
    public final void init() {
        this.collection = this.dataAccess.getCollection();
        this.customInit(this.db, this.jongo);
    }

    protected void customInit(MongoDatabase db, Jongo jongo) {
        // implement if needed
    }

    /**
     * runs a map-reduce-job on the collection. same as {@link #mapReduce(String, DBObject, DBObject, Map, MapReduceResultHandler)
     * mapReduce(name, null, null, null, conv)}
     *
     * @param <R>  the type of the result class
     * @param name the name of the map-reduce functions
     * @param conv the converter to convert the result
     * @return an {@link Iterable} with the result entries
     */
    protected final <R> Iterable<R> mapReduce(String name, final MapReduceResultHandler<R> conv) {
        return this.dataAccess.mapReduce(name, conv);
    }

    /**
     * runs a map-reduce-job on the collection. The functions are read from the classpath in the folder mongodb. The systems reads them from
     * files called &lt;name&gt;.map.js, &lt;name&gt;.reduce.js and optionally &lt;name&gt;.finalize.js. After this the result is converted
     * using the given {@link MapReduceResultHandler}
     *
     * @param <R>   the type of the result class
     * @param name  the name of the map-reduce functions
     * @param query the query to filter the elements used for the map-reduce
     * @param sort  sort query to sort elements before running map-reduce
     * @param scope the global scope for the JavaScript run
     * @param conv  the converter to convert the result
     * @return an {@link Iterable} with the result entries
     * @throws RuntimeException if resources cannot be read
     */
    protected final <R> Iterable<R> mapReduce(String name, DBObject query, DBObject sort, Map<String, Object> scope, final MapReduceResultHandler<R> conv) {
        return this.dataAccess.mapReduce(name, query, sort, scope, conv);
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
     * @param query  the query to search for
     * @param params the parameters to replace # symbols
     * @return the list of elements found
     */
    protected final List<T> findByQuery(String query, Object... params) {
        return this.dataAccess.findByQuery(query, params);
    }

    /**
     * finds all elements matching the given query and sorts them accordingly
     *
     * @param query  the query to search for
     * @param sort   the sort query to apply
     * @param params the parameters to replace # symbols
     * @return the list of elements found
     */
    protected final List<T> findSortedByQuery(String query, String sort, Object... params) {
        return this.dataAccess.findSortedByQuery(query, sort, params);
    }

    /**
     * finds all elements matching the given query and sorts them accordingly. With this method it is possible to specify a projection to
     * rename or filter fields in the result elements. Instead of returning typed objects it returns objects of type
     * <code>as</code>
     *
     * @param query      the query to search for
     * @param sort       the sort query to apply
     * @param projection the projection of fields to use
     * @param as         the target to convert result elements to
     * @param params     the parameters to replace # symbols
     * @param <P>        the element type
     * @return the list of elements found
     */
    protected final <P> List<P> findSortedByQuery(String query, String sort, String projection, Class<P> as, Object... params) {
        return this.dataAccess.findSortedByQuery(query, sort, projection, as, params);
    }

    /**
     * finds all elements matching the given query and sorts them accordingly. With this method it is possible to specify a projection to
     * rename or filter fields in the result elements. Instead of returning typed objects it returns objects converted
     * by the given {@link ResultHandler}
     *
     * @param query      the query to search for
     * @param sort       the sort query to apply
     * @param projection the projection of fields to use
     * @param handler    the handler to convert result elements with
     * @param params     the parameters to replace # symbols
     * @param <P>        the element type
     * @return the list of elements found
     */
    protected final <P> List<P> findSortedByQuery(String query, String sort, String projection, ResultHandler<P> handler, Object... params) {
        return this.dataAccess.findSortedByQuery(query, sort, projection, handler, params);
    }

    /**
     * finds all elements matching the given query and sorts them accordingly
     *
     * @param query  the query to search for
     * @param sort   the sort query to apply
     * @param skip   the number of elements to skip
     * @param limit  the number of elements to fetch
     * @param params the parameters to replace # symbols
     * @return the list of elements found
     */
    protected final List<T> findSortedByQuery(String query, String sort, Integer skip, Integer limit, Object... params) {
        return this.dataAccess.findSortedByQuery(query, sort, skip, limit, params);
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
     * @param as         the target to convert result elements to
     * @param params     the parameters to replace # symbols
     * @param <P>        the element type
     * @return the list of elements found
     */
    protected final <P> List<P> findSortedByQuery(String query, String sort, Integer skip, Integer limit, String projection, Class<P> as, Object... params) {
        return this.dataAccess.findSortedByQuery(query, sort, skip, limit, projection, as, params);
    }

    /**
     * finds all elements matching the given query and sorts them accordingly. With this method it is possible to specify a projection to
     * rename or filter fields in the result elements. Instead of returning typed objects it returns objects converted
     * by the given {@link ResultHandler}
     *
     * @param query      the query to search for
     * @param sort       the sort query to apply
     * @param skip       the number of elements to skip
     * @param limit      the number of elements to fetch
     * @param projection the projection of fields to use
     * @param handler    the handler to convert result elements with
     * @param params     the parameters to replace # symbols
     * @param <P>        the element type
     * @return the list of elements found
     */
    protected final <P> List<P> findSortedByQuery(String query, String sort, Integer skip, Integer limit, String projection, ResultHandler<P> handler, Object... params) {
        return this.dataAccess.findSortedByQuery(query, sort, skip, limit, projection, handler, params);
    }

    /**
     * finds all elements containing the given searchString in any text field and sorts them accordingly.
     *
     * @param searchString      the searchString to search for
     * @param sort       the sort query to apply
     * @return the list of elements found
     */
    protected final List<T> searchSorted(String searchString, String sort) {
        return this.dataAccess.searchSorted(searchString, sort);
    }

    /**
     * queries with the given string, sorts the result and returns the first element. <code>null</code> is returned if no element is found.
     *
     * @param query  the query string
     * @param sort   the sort string
     * @param params the parameters to replace # symbols
     * @return the first element found or <code>null</code> if none is found
     */
    protected final T findFirstByQuery(String query, String sort, Object... params) {
        return this.dataAccess.findFirstByQuery(query, sort, params).orElse(null);
    }

    @Override
    public final T findById(String id) {
        return this.dataAccess.findByObjectId(id).orElse(null);
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
        this.dataAccess.deleteByObjectId(id);
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

}
