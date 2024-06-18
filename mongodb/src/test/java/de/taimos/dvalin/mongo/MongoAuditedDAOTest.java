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
import de.taimos.dvalin.daemon.spring.InjectionUtils;
import de.taimos.dvalin.mongo.model.AuditedTestObject;
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
import java.util.Arrays;
import java.util.List;

class MongoAuditedDAOTest extends ABaseTest {

    private static final TestAuditedDAO dao = new TestAuditedDAO();

    @BeforeAll
    static void init() {
        System.out.println("MongoDataAccessTest.init()");
        try {
            Field daoField = AbstractMongoDAO.class.getDeclaredField("dataAccess");
            daoField.setAccessible(true);
            daoField.set(MongoAuditedDAOTest.dao, new MongoDBDataAccess<TestObject>(ABaseTest.database,
                InjectionUtils.createDependencyDescriptor(daoField, MongoAuditedDAOTest.dao)));

            MongoSync4Driver driver = MongoSync4Driver.withDefaultLock(ABaseTest.mongo, ABaseTest.dbName);
            driver.disableTransaction();
            MongockStandalone.builder().setDriver(driver).addMigrationScanPackage("de.taimos.dvalin.mongo.changelog")
                .setTransactionEnabled(false).setEnabled(true).buildRunner().execute();
            MongoAuditedDAOTest.dao.init();
        } catch (Exception e) {
            Assertions.fail(Arrays.toString(e.getStackTrace()));
        }
    }

    @AfterEach
    void clearDatabase() {
        MongoAuditedDAOTest.dao.dataAccess.getCollection().drop();
    }

    @Test
    void testUpdate() {
        AuditedTestObject o = new AuditedTestObject();
        o.setName("bar");
        o.setValue(new BigDecimal("5"));
        o.setDt(new DateTime(2024, 6, 7, 8, 6));
        Assertions.assertEquals("bar", o.getName());
        String id = o.getId();

        AuditedTestObject save = MongoAuditedDAOTest.dao.save(o);
        Assertions.assertEquals("bar", save.getName());
        ABaseTest.assertEquals(new BigDecimal("5"), save.getValue());
        Assertions.assertNotNull(save.getId());
        Assertions.assertNotNull(save.getDt());
        Assertions.assertEquals(o.getDt(), save.getDt());

        AuditedTestObject find = MongoAuditedDAOTest.dao.findById(id);
        Assertions.assertNotNull(find);
        Assertions.assertEquals("bar", find.getName());
        ABaseTest.assertEquals(new BigDecimal("5"), find.getValue());
        Assertions.assertEquals(id, find.getId());
        Assertions.assertNotNull(find.getDt());

        find.setName("blubb");

        AuditedTestObject save2 = MongoAuditedDAOTest.dao.save(find);
        Assertions.assertNotNull(save2);
        Assertions.assertEquals("blubb", save2.getName());
        ABaseTest.assertEquals(new BigDecimal("5"), save2.getValue());
        Assertions.assertEquals(id, save2.getId());
        Assertions.assertNotNull(save2.getDt());

        AuditedTestObject find3 = MongoAuditedDAOTest.dao.findByName("blubb");
        Assertions.assertNotNull(find3);
        Assertions.assertEquals("blubb", find3.getName());
        ABaseTest.assertEquals(new BigDecimal("5"), find3.getValue());
        Assertions.assertEquals(id, find3.getId());
        Assertions.assertNotNull(find3.getDt());

        long count = MongoAuditedDAOTest.dao.dataAccess.count(Filters.empty());
        Assertions.assertEquals(1, count);

        MongoAuditedDAOTest.dao.delete(id);

        AuditedTestObject find2 = MongoAuditedDAOTest.dao.findById(id);
        Assertions.assertNull(find2);

        count = MongoAuditedDAOTest.dao.dataAccess.count(Filters.empty());
        Assertions.assertEquals(0, count);

        ListIndexesIterable<Document> listIndexes = ABaseTest.mongo.getDatabase(ABaseTest.dbName)
            .getCollection("TestObject").listIndexes();
        for (Document index : listIndexes) {
            System.out.println(index.toString());
        }

        List<AuditedTestObject> actualHistoryElements = MongoAuditedDAOTest.dao.findHistoryElements(o.getId());
        Assertions.assertEquals(2, actualHistoryElements.size());
    }

    @Test
    void testObjectId() {
        AuditedTestObject to = new AuditedTestObject();
        to.setId("66682ae8161422626090bad3");
        to.setDt(new DateTime(2024, 4, 3, 9, 9, 3, DateTimeZone.UTC));

        MongoAuditedDAOTest.dao.dataAccess.save(to);

        Document result = MongoAuditedDAOTest.dao.dataAccess.getCollection().find().first();

        Assertions.assertNotNull(result);

        Assertions.assertEquals(new ObjectId("66682ae8161422626090bad3"), result.get("_id"));


    }
}
