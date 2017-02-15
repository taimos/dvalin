package org.springframework.data.mongodb.core;

import com.mongodb.Mongo;

/**
 * Overriding MongoTemplate from Spring Data because Mongobee has class binding at startup fails without this class.
 * Dvalin does not use Spring Data so this class is not available.
 */
public class MongoTemplate {
    
    private final Mongo mongo;
    private final String dbName;
    
    public MongoTemplate(Mongo mongo, String dbName) {
        this.mongo = mongo;
        this.dbName = dbName;
    }
    
    public Mongo getMongo() {
        return this.mongo;
    }
    
    public String getDbName() {
        return this.dbName;
    }
}
