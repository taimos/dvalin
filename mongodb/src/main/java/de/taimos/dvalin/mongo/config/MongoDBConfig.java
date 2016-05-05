package de.taimos.dvalin.mongo.config;

import com.github.mongobee.Mongobee;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MongoDBConfig {

    @Value("${mongobee.enabled:false}")
    private boolean beeEnabled;

    @Value("${mongodb.name}")
    private String dbName;

    @Value("${mongobee.basePackage}")
    private String beeBasePackage;


    @Bean
    public Mongobee mongobee(MongoClient mongoClient) {
        Mongobee bee = new Mongobee(mongoClient);
        bee.setDbName(this.dbName);
        bee.setEnabled(this.beeEnabled);
        bee.setChangeLogsScanPackage(beeBasePackage);
        return bee;
    }


}
