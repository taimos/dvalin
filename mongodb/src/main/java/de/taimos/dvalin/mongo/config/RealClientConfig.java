package de.taimos.dvalin.mongo.config;

/*-
 * #%L
 * MongoDB support for dvalin
 * %%
 * Copyright (C) 2015 - 2017 Taimos GmbH
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

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoClients;
import de.taimos.daemon.spring.conditional.OnSystemProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@OnSystemProperty(propertyName = "mongodb.type", propertyValue = "real")
@Configuration
public class RealClientConfig {

    @Value("${mongodb.uri:mongodb://${mongodb.host:localhost}:${mongodb.port:27017}}")
    private String mongoURI;

    @Value("${mongodb.sockettimeout:${mongodb.socketTimeout:10000}}")
    private int socketTimeout;

    @Value("${mongodb.connecttimeout:${mongodb.connectTimeout:10000}}")
    private int connectTimeout;


    @Bean
    public MongoClient mongoClient() {
        MongoClientOptions.Builder builder = MongoClientOptions.builder();
        builder.socketTimeout(this.socketTimeout);
        builder.connectTimeout(this.connectTimeout);

        return new MongoClient(new MongoClientURI(this.mongoURI, builder));
    }

    @Bean
    public com.mongodb.client.MongoClient mongoClient2() {
        MongoClientOptions.Builder builder = MongoClientOptions.builder();
        builder.socketTimeout(this.socketTimeout);
        builder.connectTimeout(this.connectTimeout);
        return MongoClients.create(new MongoClientURI(this.mongoURI, builder).getURI());
    }



}
