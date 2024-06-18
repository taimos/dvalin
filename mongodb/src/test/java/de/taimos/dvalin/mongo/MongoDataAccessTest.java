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

import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import de.taimos.dvalin.daemon.spring.InjectionUtils;
import de.taimos.dvalin.mongo.model.PolyTestObjectA;
import de.taimos.dvalin.mongo.model.TestObject;
import io.mongock.driver.mongodb.sync.v4.driver.MongoSync4Driver;
import io.mongock.runner.standalone.MongockStandalone;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

class MongoDataAccessTest extends ABaseTest {

    private static final TestDAO dao = new TestDAO();

    @BeforeAll
    static void init() {
        System.out.println("MongoDataAccessTest.init()");
        try {
            Field daoField = AbstractMongoDAO.class.getDeclaredField("dataAccess");
            daoField.setAccessible(true);
            daoField.set(MongoDataAccessTest.dao, new MongoDBDataAccess<TestObject>(ABaseTest.database,
                InjectionUtils.createDependencyDescriptor(daoField, MongoDataAccessTest.dao)));

            MongoSync4Driver driver = MongoSync4Driver.withDefaultLock(ABaseTest.mongo, ABaseTest.dbName);
            driver.disableTransaction();
            MongockStandalone.builder().setDriver(driver).addMigrationScanPackage("de.taimos.dvalin.mongo.changelog")
                .setTransactionEnabled(false).setEnabled(true).buildRunner().execute();
            MongoDataAccessTest.dao.init();
        } catch (Exception e) {
            Assertions.fail(Arrays.toString(e.getStackTrace()));
        }
    }

    @AfterEach
    void clearDatabase() {
        MongoDataAccessTest.dao.dataAccess.getCollection().drop();
    }

    @Test
    void testUpdate() {
        TestObject o = new TestObject();
        o.setName("bar");
        o.setValue(new BigDecimal("5"));
        o.setDt(new DateTime(2024, 6, 7, 8, 6));
        Assertions.assertEquals("bar", o.getName());
        String id = o.getId();

        TestObject save = MongoDataAccessTest.dao.save(o);
        Assertions.assertEquals("bar", save.getName());
        ABaseTest.assertEquals(new BigDecimal("5"), save.getValue());
        Assertions.assertNotNull(save.getId());
        Assertions.assertNotNull(save.getDt());
        Assertions.assertEquals(o.getDt(), save.getDt());

        TestObject find = MongoDataAccessTest.dao.findById(id);
        Assertions.assertNotNull(find);
        Assertions.assertEquals("bar", find.getName());
        ABaseTest.assertEquals(new BigDecimal("5"), find.getValue());
        Assertions.assertEquals(id, find.getId());
        Assertions.assertNotNull(find.getDt());

        find.setName("blubb");

        TestObject save2 = MongoDataAccessTest.dao.save(find);
        Assertions.assertNotNull(save2);
        Assertions.assertEquals("blubb", save2.getName());
        ABaseTest.assertEquals(new BigDecimal("5"), save2.getValue());
        Assertions.assertEquals(id, save2.getId());
        Assertions.assertNotNull(save2.getDt());

        TestObject find3 = MongoDataAccessTest.dao.findByName("blubb");
        Assertions.assertNotNull(find3);
        Assertions.assertEquals("blubb", find3.getName());
        ABaseTest.assertEquals(new BigDecimal("5"), find3.getValue());
        Assertions.assertEquals(id, find3.getId());
        Assertions.assertNotNull(find3.getDt());

        long count = MongoDataAccessTest.dao.dataAccess.count(Filters.empty());
        Assertions.assertEquals(1, count);

        MongoDataAccessTest.dao.delete(id);

        TestObject find2 = MongoDataAccessTest.dao.findById(id);
        Assertions.assertNull(find2);

        count = MongoDataAccessTest.dao.dataAccess.count(Filters.empty());
        Assertions.assertEquals(0, count);

        ListIndexesIterable<Document> listIndexes = ABaseTest.mongo.getDatabase(ABaseTest.dbName)
            .getCollection("TestObject").listIndexes();
        for (Document index : listIndexes) {
            System.out.println(index.toString());
        }
    }

    @Test
    void testObjectId() {
        TestObject to = new TestObject();
        to.setId("66682ae8161422626090bad3");
        to.setDt(new DateTime(2024, 4, 3, 9, 9, 3, DateTimeZone.UTC));

        MongoDataAccessTest.dao.dataAccess.save(to);

        Document result = MongoDataAccessTest.dao.dataAccess.getCollection().find().first();

        Assertions.assertNotNull(result);

        Assertions.assertEquals(new ObjectId("66682ae8161422626090bad3"), result.get("_id"));
    }

    @Test
    void testPoly() {
        TestObject objectA = new TestObject();
        objectA.setName("foo");
        objectA.setValue(new BigDecimal("5"));
        objectA.setDt(new DateTime(2024, 6, 7, 8, 6));
        MongoDataAccessTest.dao.save(objectA);

        PolyTestObjectA objectB = new PolyTestObjectA();
        objectB.setName("bar");
        objectB.setField("foo");
        objectB.setValue(new BigDecimal("6.0"));
        objectB.setDt(new DateTime(2024, 4, 7, 11, 6));
        MongoDataAccessTest.dao.save(objectB);

        long amountOfObjects = MongoDataAccessTest.dao.dataAccess.getCollection().countDocuments();
        Assertions.assertEquals(2L, amountOfObjects);

        ArrayList<Document> result = MongoDataAccessTest.dao.dataAccess.getCollection() //
            .find(Document.class) //
            .sort(Sorts.ascending("name")) //
            .into(new ArrayList<>());
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(TestObject.class.getCanonicalName(), result.get(1).get("clazz"));
        Assertions.assertEquals(PolyTestObjectA.class.getCanonicalName(), result.get(0).get("clazz"));
    }
}
