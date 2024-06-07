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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.jongo.Find;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.ResultHandler;
import org.jongo.Update;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.util.StreamUtils;

import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceOutput;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;

import de.taimos.dvalin.daemon.spring.InjectionUtils;

@Repository
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MongoDBDataAccess<T> {

    /**
     * Query to perform a full text search
     */
    public static final String FULLTEXT_QUERY = "{\"$text\" : {\"$search\" : #}}";

    private final MongoDatabase db;
    private final Class<T> entityClass;
    private final MongoCollection collection;

    @Autowired
    public MongoDBDataAccess(Jongo jongo, MongoDatabase db, InjectionPoint ip) {
        this.db = db;
        this.entityClass = (Class<T>) InjectionUtils.getGenericType(ip);
        this.collection = jongo.getCollection(this.getCollectionName());
    }

    public String getCollectionName() {
        return this.entityClass.getSimpleName();
    }

    public Class<T> getEntityClass() {
        return this.entityClass;
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
    public final <R> Iterable<R> mapReduce(String name, final MapReduceResultHandler<R> conv) {
        return this.mapReduce(name, null, null, null, conv);
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
    public final <R> Iterable<R> mapReduce(String name, DBObject query, DBObject sort, Map<String, Object> scope, final MapReduceResultHandler<R> conv) {
        String map = this.getMRFunction(name, "map");
        String reduce = this.getMRFunction(name, "reduce");

        MapReduceCommand mrc = new MapReduceCommand(this.collection.getDBCollection(), map, reduce, null, MapReduceCommand.OutputType.INLINE, query);
        String finalizeFunction = this.getMRFunction(name, "finalize");
        if(finalizeFunction != null) {
            mrc.setFinalize(finalizeFunction);
        }
        if(sort != null) {
            mrc.setSort(sort);
        }
        if(scope != null) {
            mrc.setScope(scope);
        }
        MapReduceOutput mr = this.collection.getDBCollection().mapReduce(mrc);
        return new ConverterIterable<>(mr.results().iterator(), conv);
    }

    private String getMRFunction(String name, String type) {
        try {
            InputStream stream = this.getClass().getResourceAsStream("/mongodb/" + name + "." + type + ".js");
            if(stream != null) {
                return StreamUtils.copyToString(stream, Charset.defaultCharset());
            }
            return null;
        } catch(IOException e) {
            throw new RuntimeException("Failed to read resource", e);
        }
    }

    public final List<T> findList() {
        Iterable<T> as = this.collection.find().sort("{_id:1}").as(this.entityClass);
        return this.convertIterable(as);
    }

    public List<T> findList(String sortProp, Integer sortDirection, Integer limit, Integer skip) {
        return this.findSortedByQuery("{}", "{" + sortProp + ":" + sortDirection + "}", skip, limit);
    }

    /**
     * converts the given {@link Iterable} to a {@link List}
     *
     * @param <P> the element type
     * @param as  the {@link Iterable}
     * @return the converted {@link List}
     */
    public final <P> List<P> convertIterable(Iterable<P> as) {
        List<P> objects = new ArrayList<>();
        for(P mp : as) {
            objects.add(mp);
        }
        return objects;
    }

    /**
     * finds all elements matching the given query
     *
     * @param query  the query to search for
     * @param params the parameters to replace # symbols
     * @return the list of elements found
     */
    public final List<T> findByQuery(String query, Object... params) {
        return this.findSortedByQuery(query, null, params);
    }

    /**
     * finds all elements matching the given query and sorts them accordingly
     *
     * @param query  the query to search for
     * @param sort   the sort query to apply
     * @param params the parameters to replace # symbols
     * @return the list of elements found
     */
    public final List<T> findSortedByQuery(String query, String sort, Object... params) {
        return this.findSortedByQuery(query, sort, null, (Integer) null, params);
    }

    /**
     * finds all elements matching the given query and sorts them accordingly. With this method it is possible to specify a projection to
     * rename or filter fields in the result elements. Instead of returning objects it returns objects of type
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
    public final <P> List<P> findSortedByQuery(String query, String sort, String projection, Class<P> as, Object... params) {
        return this.findSortedByQuery(query, sort, null, null, projection, as, params);
    }

    /**
     * finds all elements matching the given query and sorts them accordingly. With this method it is possible to specify a projection to
     * rename or filter fields in the result elements. Instead of returning objects it returns objects converted
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
        return this.findSortedByQuery(query, sort, null, null, projection, handler, params);
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
    public final List<T> findSortedByQuery(String query, String sort, Integer skip, Integer limit, Object... params) {
        return this.findSortedByQuery(query, sort, skip, limit, null, this.entityClass, params);
    }

    /**
     * finds all elements matching the given query and sorts them accordingly. With this method it is possible to specify a projection to
     * rename or filter fields in the result elements. Instead of returning objects it returns objects of type
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
    public final <P> List<P> findSortedByQuery(String query, String sort, Integer skip, Integer limit, String projection, Class<P> as, Object... params) {
        Find find = this.createFind(query, sort, skip, limit, projection, params);
        return this.convertIterable(find.as(as));
    }

    /**
     * finds all elements matching the given query and sorts them accordingly. With this method it is possible to specify a projection to
     * rename or filter fields in the result elements. Instead of returning objects it returns objects converted
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
    public final <P> List<P> findSortedByQuery(String query, String sort, Integer skip, Integer limit, String projection, ResultHandler<P> handler, Object... params) {
        Find find = this.createFind(query, sort, skip, limit, projection, params);
        return this.convertIterable(find.map(handler));
    }

    private Find createFind(String query, String sort, Integer skip, Integer limit, String projection, Object... params) {
        Find find = this.collection.find(query, params);
        if((sort != null) && !sort.isEmpty()) {
            find.sort(sort);
        }
        if((projection != null) && !projection.isEmpty()) {
            find.projection(projection);
        }
        if(skip != null) {
            find.skip(skip);
        }
        if(limit != null) {
            find.limit(limit);
        }
        return find;
    }

    /**
     * finds all elements containing the given searchString in any text field and sorts them accordingly.
     *
     * @param searchString      the searchString to search for
     * @param sort       the sort query to apply
     * @return the list of elements found
     */
    public final List<T> searchSorted(String searchString, String sort) {
        return this.findSortedByQuery(MongoDBDataAccess.FULLTEXT_QUERY, sort, searchString);
    }

    /**
     * queries with the given string, sorts the result and returns the first element. <code>null</code> is returned if no element is found.
     *
     * @param query  the query string
     * @param sort   the sort string
     * @param params the parameters to replace # symbols
     * @return the first element found or <code>null</code> if none is found
     */
    public final Optional<T> findFirstByQuery(String query, String sort, Object... params) {
        Find find = this.collection.find(query, params);
        if((sort != null) && !sort.isEmpty()) {
            find.sort(sort);
        }
        Iterable<T> as = find.limit(1).as(this.entityClass);
        Iterator<T> iterator = as.iterator();
        if(iterator.hasNext()) {
            return Optional.of(iterator.next());
        }
        return Optional.empty();
    }

    public final Optional<T> findByObjectId(String id) {
        return Optional.ofNullable(this.collection.findOne(new ObjectId(id)).as(this.entityClass));
    }

    public final Optional<T> findByStringId(String id) {
        return Optional.ofNullable(this.collection.findOne("{\"_id\":#}", id).as(this.entityClass));
    }

    /**
     * @param query the query string
     * @param parameter the parameters to replace # symbols
     * @return the number of elements matching the query
     */
    public final long count(String query, Object... parameter) {
        return this.collection.count(query, parameter);
    }

    public final T save(T object) {
        this.collection.save(object);
        return object;
    }

    public final void deleteByObjectId(String id) {
        this.collection.remove(new ObjectId(id));
    }

    public final void deleteByStringId(String id) {
        this.collection.remove("{\"_id\":#}", id);
    }

    public final void delete(String query, Object... parameter) {
        this.collection.remove(query, parameter);
    }

    public final Update update(ObjectId id) {
        return this.collection.update(id);
    }

    public final Update update(String query) {
        return this.collection.update(query);
    }

    public final Update update(String query, Object... parameter) {
        return this.collection.update(query, parameter);
    }

    @Deprecated
    public MongoCollection getCollection() {
        return this.collection;
    }

    public GridFSBucket getGridFSBucket(String bucket) {
        if(bucket == null || bucket.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return GridFSBuckets.create(this.db, bucket);
    }

}
