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
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClients;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import de.taimos.daemon.spring.conditional.OnSystemProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@OnSystemProperty(propertyName = "mongodb.type", propertyValue = "fake")
@Configuration
public class FakeClientConfig {


    @Bean
    public MongoServer mongoServer() {
        return new MongoServer(new MemoryBackend());
    }


    @Bean
    public MongoClient mongoClient(MongoServer mongoServer) {
        return new MongoClient(new ServerAddress(mongoServer.bind()));
    }

    @Bean
    public com.mongodb.client.MongoClient mongoClient2(MongoServer mongoServer) {
        return MongoClients.create(mongoServer.bindAndGetConnectionString());
    }

}
