package de.taimos.dvalin.mongo.config;

import com.github.fakemongo.Fongo;
import com.mongodb.MongoClient;
import de.taimos.daemon.spring.conditional.OnSystemProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@OnSystemProperty(propertyName = "mongodb.type", propertyValue = "fake")
@Configuration
public class FakeClientConfig {


    @Bean
    public Fongo fongo() {
        return new Fongo("InMemoryStore");
    }


    @Bean
    public MongoClient mongoClient(Fongo fongo) {
        return fongo.getMongo();
    }

}
