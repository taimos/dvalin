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

import org.jongo.Jongo;
import org.junit.Assert;

import com.github.fakemongo.Fongo;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import de.taimos.daemon.log4j.Log4jLoggingConfigurer;

/**
 * Copyright 2015 Taimos GmbH<br>
 * <br>
 *
 * @author thoeger
 */
public class ABaseTest {

    protected static final String dbName = "dvalin-mongo";
    public static final MongoClient mongo = new Fongo("InMemory").getMongo();
    public static final Jongo jongo = JongoFactory.createDefault(ABaseTest.mongo.getDB(ABaseTest.dbName));
    public static final MongoDatabase database = ABaseTest.mongo.getDatabase(ABaseTest.dbName);

    static {
        try {
            new Log4jLoggingConfigurer().simpleLogging();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected static void assertEquals(BigDecimal bd1, BigDecimal bd2) {
        Assert.assertEquals(bd1.doubleValue(), bd2.doubleValue(), 0);
    }

    /**
     *
     */
    public ABaseTest() {
        super();
    }

}
