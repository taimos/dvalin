package de.taimos.dvalin.mongo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;

import de.taimos.daemon.spring.conditional.OnSystemProperty;


@OnSystemProperty(propertyName = "mongodb.type", propertyValue = "real")
@Configuration
public class RealClientConfig {

    @Value("${mongodb.uri:mongodb://${mongodb.host:localhost}:${mongodb.port:27017}}")
    private String mongoURI;

    @Value("${mongodb.socketTimeout:10000}")
    private int socketTimeout;

    @Value("${mongodb.connectTimeout:10000}")
    private int connectTimeout;


    @Bean
    public MongoClient mongoClient() {
        MongoClientOptions.Builder builder = MongoClientOptions.builder();
        builder.socketTimeout(this.socketTimeout);
        builder.connectTimeout(this.connectTimeout);

        return new MongoClient(new MongoClientURI(this.mongoURI, builder));
    }



}
