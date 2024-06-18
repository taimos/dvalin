/**
 *
 */
package de.taimos.dvalin.mongo;

/*
 * #%L
 * MongoDB support for dvalin
 * %%
 * Copyright (C) 2015 Taimos GmbH
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

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.ServerVersion;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import de.taimos.daemon.log4j.Log4jLoggingConfigurer;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.junit.jupiter.api.Assertions;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.bson.codecs.pojo.Conventions.DEFAULT_CONVENTIONS;

/**
 * Copyright 2015 Taimos GmbH<br>
 * <br>
 *
 * @author thoeger
 */
public class ABaseTest {

    public static final String dbName = "dvalin-mongo";

    private static final ServerAddress serverAddress = new ServerAddress(
        new MongoServer(new MemoryBackend().version(ServerVersion.MONGO_3_6)).bind());

    public static final MongoClient mongo = MongoClients.create(new ConnectionString(
        String.format("mongodb://%s:%d", ABaseTest.serverAddress.getHost(), ABaseTest.serverAddress.getPort())));

    public static final MongoDatabase database = ABaseTest.mongo.getDatabase(ABaseTest.dbName).withCodecRegistry(
        CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
            CodecRegistries.fromCodecs(new JodaCodec()),
            CodecRegistries.fromProviders(
                PojoCodecProvider.builder().conventions(DEFAULT_CONVENTIONS).automatic(true).build())));

    static {
        try {
            new Log4jLoggingConfigurer().simpleLogging();
        } catch (Exception e) {
            Assertions.fail(Arrays.toString(e.getStackTrace()));
        }
    }


    protected static void assertEquals(BigDecimal bd1, BigDecimal bd2) {
        Assertions.assertEquals(bd1.doubleValue(), bd2.doubleValue(), 0);
    }

    /**
     *
     */
    public ABaseTest() {
        super();
    }

}
