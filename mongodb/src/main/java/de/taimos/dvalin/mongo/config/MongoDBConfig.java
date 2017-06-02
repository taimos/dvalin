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
    
    @Value("${mongobee.basepackage:${mongobee.basePackage:}}")
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
