package de.taimos.dao.mongo.links;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import javax.annotation.PostConstruct;

import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.DB;
import com.mongodb.MongoClient;

import de.taimos.dao.mongo.JongoFactory;

public class DLinkDAO implements IDLinkDAO {
	
	@Autowired
	private MongoClient mongo;
	
	private Jongo jongo;
	
	
	@PostConstruct
	public final void init() {
		String dbName = System.getProperty("mongodb.name");
		if (dbName == null) {
			throw new RuntimeException("Missing database name; Set system property 'mongodb.name'");
		}
		DB db = this.mongo.getDB(dbName);
		this.jongo = JongoFactory.createDefault(db);
	}
	
	@Override
	public <T extends AReferenceableEntity<T>> T resolve(DocumentLink<T> link) {
		MongoCollection collection = this.jongo.getCollection(link.getTargetClass().getSimpleName());
		return collection.findOne(new ObjectId(link.getObjectId())).as(link.getTargetClass());
	}
	
	@Override
	public <T extends AReferenceableEntity<T>> List<T> resolve(List<DocumentLink<T>> links, Class<T> targetClass) {
		MongoCollection collection = this.jongo.getCollection(targetClass.getSimpleName());
		List<ObjectId> ids = new ArrayList<>();
		for (DocumentLink<T> link : links) {
			if (!link.getTargetClass().equals(targetClass)) {
				throw new IllegalArgumentException("Invalid link in collection");
			}
			ids.add(new ObjectId(link.getObjectId()));
		}
		Iterator<T> it = collection.find("{\"_id\" : {\"$in\" : #}}", ids).as(targetClass).iterator();
		List<T> resolved = new ArrayList<>();
		while (it.hasNext()) {
			resolved.add(it.next());
		}
		return resolved;
	}
	
}
