package de.taimos.dao.mongo;

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


import java.io.IOException;
import java.util.Scanner;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;

/**
 * Copyright 2014 Taimos GmbH<br>
 * <br>
 * 
 * Initialized the database with data found in the classpath. It searches for files in the folder mongodb ending with .ndjson. The filename
 * is used as name of the collection. The contents of this file has to be valid ND-JSON which means that it contains one JSON Object per
 * line.
 *
 * @author Thorsten Hoeger
 *
 */
public class MongoDBInit {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBInit.class);
	
	@Autowired
	private MongoClient mongo;
	
	private boolean enabled = false;
	
	
	/**
	 * init database with demo data
	 */
	@PostConstruct
	public void initDatabase() {
		if (!this.enabled) {
			// exit if we are disabled
			return;
		}
		MongoDBInit.LOGGER.info("initializing MongoDB");
		String dbName = System.getProperty("mongodb.name");
		if (dbName == null) {
			throw new RuntimeException("Missing database name; Set system property 'mongodb.name'");
		}
		MongoDatabase db = this.mongo.getDatabase(dbName);
		
		try {
			PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
			Resource[] resources = resolver.getResources("classpath*:mongodb/*.ndjson");
			MongoDBInit.LOGGER.info("Scanning for collection data");
			for (Resource res : resources) {
				String filename = res.getFilename();
				String collection = filename.substring(0, filename.length() - 7);
				MongoDBInit.LOGGER.info("Found collection file: " + collection);
				MongoCollection<DBObject> dbCollection = db.getCollection(collection, DBObject.class);
				try (Scanner scan = new Scanner(res.getInputStream())) {
					int lines = 0;
					while (scan.hasNextLine()) {
						String json = scan.nextLine();
						Object parse = JSON.parse(json);
						if (parse instanceof DBObject) {
							DBObject dbObject = (DBObject) parse;
							dbCollection.insertOne(dbObject);
						} else {
							MongoDBInit.LOGGER.error("Invalid object found: " + parse);
							throw new RuntimeException("Invalid object");
						}
						lines++;
					}
					MongoDBInit.LOGGER.info("Imported " + lines + " objects into collection " + collection);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Error importing objects", e);
		}
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
}
