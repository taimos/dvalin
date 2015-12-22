package de.taimos.dao.mongo.links;

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jongo.MongoCollection;
import org.jongo.ResultHandler;

import com.mongodb.DBObject;

/**
 * QueryHelper to convert query result to a list of DLinks. It only queries the fields necessary to construct links.
 * 
 * @author Thorsten Hoeger
 *
 * @param <T> the {@link AReferenceableEntity} this link uses
 */
public class DLinkQuery<T extends AReferenceableEntity<T>> {
	
	private Class<T> targetClass;
	
	private String labelField;
	
	
	/**
	 * @param targetClass the links target class
	 * @param labelField the name of the label field
	 */
	public DLinkQuery(Class<T> targetClass, String labelField) {
		this.targetClass = targetClass;
		this.labelField = labelField;
	}
	
	public List<DocumentLink<T>> find(MongoCollection collection, String query, Object... parameter) {
		ResultHandler<DocumentLink<T>> handler = new ResultHandler<DocumentLink<T>>() {
			
			@Override
			public DocumentLink<T> map(DBObject result) {
				if (!result.containsField("_id") || !result.containsField(DLinkQuery.this.labelField)) {
					throw new RuntimeException("Fields missing to construct DocumentLink");
				}
				String id = result.get("_id").toString();
				String label = result.get(DLinkQuery.this.labelField).toString();
				return new DocumentLink<T>(DLinkQuery.this.targetClass, id, label);
			}
		};
		Iterator<DocumentLink<T>> it = collection.find(query, parameter).projection(String.format("{%s:1}", this.labelField)).map(handler).iterator();
		
		List<DocumentLink<T>> objects = new ArrayList<>();
		while (it.hasNext()) {
			DocumentLink<T> link = it.next();
			objects.add(link);
		}
		return objects;
	}
	
}
