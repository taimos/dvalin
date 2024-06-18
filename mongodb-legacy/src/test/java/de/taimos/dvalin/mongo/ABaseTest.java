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

import java.math.BigDecimal;

import com.mongodb.ConnectionString;
import com.mongodb.DB;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.ServerVersion;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import de.taimos.daemon.log4j.Log4jLoggingConfigurer;
import org.jongo.Jongo;
import org.junit.jupiter.api.Assertions;

/**
 * Copyright 2015 Taimos GmbH<br>
 * <br>
 *
 * @author thoeger
 */
public class ABaseTest {

    protected static final String dbName = "dvalin-mongo";
    private static final ServerAddress serverAddress = new ServerAddress(new MongoServer(new MemoryBackend().version(ServerVersion.MONGO_3_6)).bind());

    public static final MongoClient mongo = MongoClients.create(new ConnectionString(String.format("mongodb://%s:%d", ABaseTest.serverAddress.getHost(), ABaseTest.serverAddress.getPort())));
    public static final com.mongodb.MongoClient oldMongo = new com.mongodb.MongoClient(new ServerAddress(new MongoServer(new MemoryBackend().version(ServerVersion.MONGO_3_6)).bind()));;

    public static final DB oldDB = ABaseTest.oldMongo.getDB(ABaseTest.dbName);
    public static final Jongo jongo = JongoFactory.createDefault(ABaseTest.oldMongo.getDB(ABaseTest.dbName));
    public static final MongoDatabase database = ABaseTest.mongo.getDatabase(ABaseTest.dbName);

    static {
        try {
            new Log4jLoggingConfigurer().simpleLogging();
        } catch (Exception e) {
            e.printStackTrace();
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
