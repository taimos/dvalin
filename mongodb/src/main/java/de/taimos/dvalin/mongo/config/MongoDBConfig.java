package de.taimos.dvalin.mongo.config;

import org.jongo.Jongo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.mongobee.Mongobee;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import de.taimos.dvalin.mongo.JongoFactory;

@Configuration
public class MongoDBConfig {
    
    @Value("${mongobee.enabled:false}")
    private boolean beeEnabled;
    
    @Value("${mongodb.name}")
    private String dbName;
    
    @Value("${mongobee.basePackage:}")
    private String beeBasePackage;
    
    @Bean
    public Mongobee mongobee(MongoClient mongoClient, Jongo jongo) {
        Mongobee bee = new Mongobee(mongoClient);
        bee.setDbName(this.dbName);
        bee.setEnabled(this.beeEnabled);
        bee.setChangeLogsScanPackage(this.beeBasePackage);
        bee.setJongo(jongo);
        return bee;
    }
    
    @Bean
    public MongoDatabase mongoDatabase(MongoClient mongoClient) {
        return mongoClient.getDatabase(this.dbName);
    }
    
    @Bean
    public Jongo jongo(MongoClient mongoClient) {
        return JongoFactory.createDefault(mongoClient.getDB(dbName));
    }
    
}
