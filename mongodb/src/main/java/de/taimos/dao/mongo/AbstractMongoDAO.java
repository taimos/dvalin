package de.taimos.dao.mongo;

/*
 * #%L Spring DAO Mongo %% Copyright (C) 2013 Taimos GmbH %% Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License. #L%
 */

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.bson.types.ObjectId;
import org.jongo.Find;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.ResultHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;

import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceCommand.OutputType;
import com.mongodb.MapReduceOutput;
import com.mongodb.MongoClient;

import de.taimos.dao.AEntity;
import de.taimos.dao.ICrudDAO;

/**
 * Copyright 2015 Hoegernet<br>
 * <br>
 * abstract class to derive to implement Mongo DAOs. The system property <code>mongodb.name</code> has to be set and it denotes the database
 * used for this DAO.
 * 
 * @author Thorsten Hoeger
 * 		
 * @param <T> the entity this DAO is used for
 */
public abstract class AbstractMongoDAO<T extends AEntity> implements ICrudDAO<T> {
	
	@Autowired
	private MongoClient mongo;
	
	private Jongo jongo;
	protected MongoCollection collection;
	
	
	@PostConstruct
	public final void init() {
		String dbName = System.getProperty("mongodb.name");
		if (dbName == null) {
			throw new RuntimeException("Missing database name; Set system property 'mongodb.name'");
		}
		DB db = this.mongo.getDB(dbName);
		this.jongo = this.createJongo(db);
		this.collection = this.jongo.getCollection(this.getCollectionName());
	}
	
	/**
	 * @return the name of the mongo collection<br>
	 *         defaults to the entity's simple name
	 */
	protected String getCollectionName() {
		return this.getEntityClass().getSimpleName();
	}
	
	/**
	 * @return the entity class of this DAO
	 */
	protected abstract Class<T> getEntityClass();
	
	/**
	 * runs a map-reduce-job on the collection. same as {@link #mapReduce(String, DBObject, DBObject, Map, MapReduceResultHandler)
	 * mapReduce(name, null, null, null, conv)}
	 * 
	 * @param <R> the type of the result class
	 * @param name the name of the map-reduce functions
	 * @param conv the converter to convert the result
	 * @return an {@link Iterable} with the result entries
	 */
	protected final <R> Iterable<R> mapReduce(String name, final MapReduceResultHandler<R> conv) {
		return this.mapReduce(name, null, null, null, conv);
	}
	
	/**
	 * runs a map-reduce-job on the collection. The functions are read from the classpath in the folder mongodb. The systems reads them from
	 * files called &lt;name&gt;.map.js, &lt;name&gt;.reduce.js and optionally &lt;name&gt;.finalize.js. After this the result is converted
	 * using the given {@link MapReduceResultHandler}
	 * 
	 * @param <R> the type of the result class
	 * @param name the name of the map-reduce functions
	 * @param query the query to filter the elements used for the map-reduce
	 * @param sort sort query to sort elements before running map-reduce
	 * @param scope the global scope for the JavaScript run
	 * @param conv the converter to convert the result
	 * @return an {@link Iterable} with the result entries
	 * @throws RuntimeException if resources cannot be read
	 */
	protected final <R> Iterable<R> mapReduce(String name, DBObject query, DBObject sort, Map<String, Object> scope, final MapReduceResultHandler<R> conv) {
		String map = this.getMRFunction(name, "map");
		String reduce = this.getMRFunction(name, "reduce");
		
		MapReduceCommand mrc = new MapReduceCommand(this.collection.getDBCollection(), map, reduce, null, OutputType.INLINE, query);
		String finalizeFunction = this.getMRFunction(name, "finalize");
		if (finalizeFunction != null) {
			mrc.setFinalize(finalizeFunction);
		}
		if (sort != null) {
			mrc.setSort(sort);
		}
		if (scope != null) {
			mrc.setScope(scope);
		}
		MapReduceOutput mr = this.collection.getDBCollection().mapReduce(mrc);
		return new ConverterIterable<R>(mr.results().iterator(), conv);
	}
	
	private String getMRFunction(String name, String type) {
		try {
			InputStream stream = this.getClass().getResourceAsStream("/mongodb/" + name + "." + type + ".js");
			if (stream != null) {
				return StreamUtils.copyToString(stream, Charset.defaultCharset());
			}
			return null;
		} catch (IOException e) {
			throw new RuntimeException("Failed to read resource", e);
		}
	}
	
	@Override
	public final List<T> findList() {
		Iterable<T> as = this.collection.find().sort("{_id:1}").as(this.getEntityClass());
		return this.convertIterable(as);
	}
	
	/**
	 * converts the given {@link Iterable} to a {@link List}
	 * 
	 * @param
	 * 			<P>
	 *            the element type
	 * @param as the {@link Iterable}
	 * @return the converted {@link List}
	 */
	protected final <P> List<P> convertIterable(Iterable<P> as) {
		List<P> objects = new ArrayList<>();
		for (P mp : as) {
			objects.add(mp);
		}
		return objects;
	}
	
	/**
	 * finds all elements matching the given query
	 * 
	 * @param query the query to search for
	 * @param params the parameters to replace # symbols
	 * @return the list of elements found
	 */
	protected final List<T> findByQuery(String query, Object... params) {
		return this.findSortedByQuery(query, null, params);
	}
	
	/**
	 * finds all elements matching the given query and sorts them accordingly
	 * 
	 * @param query the query to search for
	 * @param sort the sort query to apply
	 * @param params the parameters to replace # symbols
	 * @return the list of elements found
	 */
	protected final List<T> findSortedByQuery(String query, String sort, Object... params) {
		return this.findSortedByQuery(query, sort, null, this.getEntityClass(), params);
	}
	
	/**
	 * finds all elements matching the given query and sorts them accordingly. With this method it is possible to specify a projection to
	 * rename or filter fields in the result elements. Instead of returning {@link #getEntityClass()} objects it returns objects of type
	 * <code>as</code>
	 * 
	 * @param query the query to search for
	 * @param sort the sort query to apply
	 * @param projection the projection of fields to use
	 * @param as the target to convert result elements to
	 * @param params the parameters to replace # symbols
	 * @param
	 * 			<P>
	 *            the element type
	 * @return the list of elements found
	 */
	protected final <P> List<P> findSortedByQuery(String query, String sort, String projection, Class<P> as, Object... params) {
		Find find = this.createFind(query, sort, projection, params);
		return this.convertIterable(find.as(as));
	}
	
	/**
	 * finds all elements matching the given query and sorts them accordingly. With this method it is possible to specify a projection to
	 * rename or filter fields in the result elements. Instead of returning {@link #getEntityClass()} objects it returns objects converted
	 * by the given {@link ResultHandler}
	 * 
	 * @param query the query to search for
	 * @param sort the sort query to apply
	 * @param projection the projection of fields to use
	 * @param handler the handler to convert result elements with
	 * @param params the parameters to replace # symbols
	 * @param
	 * 			<P>
	 *            the element type
	 * @return the list of elements found
	 */
	protected final <P> List<P> findSortedByQuery(String query, String sort, String projection, ResultHandler<P> handler, Object... params) {
		Find find = this.createFind(query, sort, projection, params);
		return this.convertIterable(find.map(handler));
	}
	
	private Find createFind(String query, String sort, String projection, Object... params) {
		Find find = this.collection.find(query, params);
		if ((sort != null) && !sort.isEmpty()) {
			find.sort(sort);
		}
		if ((projection != null) && !projection.isEmpty()) {
			find.projection(projection);
		}
		return find;
	}
	
	/**
	 * queries with the given string, sorts the result and returns the first element. <code>null</code> is returned if no element is found.
	 * 
	 * @param query the query string
	 * @param sort the sort string
	 * @param params the parameters to replace # symbols
	 * @return the first element found or <code>null</code> if none is found
	 */
	protected final T findFirstByQuery(String query, String sort, Object... params) {
		Find find = this.collection.find(query, params);
		if ((sort != null) && !sort.isEmpty()) {
			find.sort(sort);
		}
		Iterable<T> as = find.limit(1).as(this.getEntityClass());
		Iterator<T> iterator = as.iterator();
		if (iterator.hasNext()) {
			return iterator.next();
		}
		return null;
	}
	
	@Override
	public final T findById(String id) {
		return this.collection.findOne(new ObjectId(id)).as(this.getEntityClass());
	}
	
	@Override
	public final T save(T object) {
		this.beforeSave(object);
		this.collection.save(object);
		this.afterSave(object);
		return object;
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
		this.collection.remove(new ObjectId(id));
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
	
	/**
	 * creates the Jongo driver instance. Override to manipulate the Jongo driver
	 * 
	 * @param db the database to use with the created Jongo
	 * @return the created Jongo driver
	 */
	protected Jongo createJongo(DB db) {
		return JongoFactory.createDefault(db);
	}
}