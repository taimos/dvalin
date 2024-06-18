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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.ListIndexesIterable;
import de.taimos.dvalin.daemon.spring.InjectionUtils;
import io.mongock.driver.mongodb.sync.v4.driver.MongoSync4Driver;
import io.mongock.runner.standalone.MongockStandalone;
import org.bson.Document;
import org.joda.time.DateTime;
import org.jongo.Mapper;
import org.jongo.marshall.jackson.JacksonMapper;
import org.jongo.marshall.jackson.JacksonMapper.Builder;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;

public class Tester extends ABaseTest {

    private static final TestDAO dao = new TestDAO();

    @BeforeClass
    public static void init() {
        try {
            Field mongoField = AbstractMongoDAO.class.getDeclaredField("mongo");
            mongoField.setAccessible(true);
            mongoField.set(Tester.dao, ABaseTest.oldMongo);

            Field jongoField = AbstractMongoDAO.class.getDeclaredField("jongo");
            jongoField.setAccessible(true);
            jongoField.set(Tester.dao, ABaseTest.jongo);

            Field dbField = AbstractMongoDAO.class.getDeclaredField("db");
            dbField.setAccessible(true);
            dbField.set(Tester.dao, ABaseTest.database);

            Field daoField = AbstractMongoDAO.class.getDeclaredField("dataAccess");
            daoField.setAccessible(true);
            daoField.set(Tester.dao, new MongoDBDataAccess<TestObject>(ABaseTest.jongo, ABaseTest.database,
                InjectionUtils.createDependencyDescriptor(daoField, Tester.dao)));

            MongoSync4Driver driver = MongoSync4Driver.withDefaultLock(ABaseTest.mongo, ABaseTest.dbName);
            driver.disableTransaction();
            MongockStandalone.builder().setDriver(driver).addMigrationScanPackage("de.taimos.dvalin.mongo.changelog")
                .setTransactionEnabled(false).setEnabled(true).buildRunner().execute();
            Tester.dao.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUpdate() {
        TestObject o = new TestObject();
        o.setName("bar");
        o.setValue(new BigDecimal("5"));
        Assert.assertEquals("bar", o.getName());
        String id = o.getId();

        TestObject save = Tester.dao.save(o);
        Assert.assertEquals("bar", save.getName());
        ABaseTest.assertEquals(new BigDecimal("5"), save.getValue());
        Assert.assertNotNull(save.getId());
        Assert.assertNotNull(save.getDt());

        TestObject find = Tester.dao.findById(id);
        Assert.assertNotNull(find);
        Assert.assertEquals("bar", find.getName());
        ABaseTest.assertEquals(new BigDecimal("5"), find.getValue());
        Assert.assertEquals(id, find.getId());
        Assert.assertNotNull(find.getDt());

        find.setName("blubb");

        TestObject save2 = Tester.dao.save(find);
        Assert.assertNotNull(save2);
        Assert.assertEquals("blubb", save2.getName());
        ABaseTest.assertEquals(new BigDecimal("5"), save2.getValue());
        Assert.assertEquals(id, save2.getId());
        Assert.assertNotNull(save2.getDt());

        TestObject find3 = Tester.dao.findByName("blubb");
        Assert.assertNotNull(find3);
        Assert.assertEquals("blubb", find3.getName());
        ABaseTest.assertEquals(new BigDecimal("5"), find3.getValue());
        Assert.assertEquals(id, find3.getId());
        Assert.assertNotNull(find3.getDt());

        long count = Tester.dao.dataAccess.count("{}");
        Assert.assertEquals(1, count);

        Tester.dao.delete(id);

        TestObject find2 = Tester.dao.findById(id);
        Assert.assertNull(find2);

        count = Tester.dao.dataAccess.count("{}");
        Assert.assertEquals(0, count);

        ListIndexesIterable<Document> listIndexes = ABaseTest.mongo.getDatabase(ABaseTest.dbName)
            .getCollection("TestObject").listIndexes();
        for (Document index : listIndexes) {
            System.out.println(index.toString());
        }
    }

    @Test
    public void serialize() throws Exception {
        TestObject o = new TestObject();
        o.setName("bar");
        o.setValue(new BigDecimal("5"));
        Assert.assertEquals("bar", o.getName());

        DBObject dbObject = this.createMapper().getMarshaller().marshall(o).toDBObject();
        System.out.println(dbObject);
        String json = dbObject.toString();
        System.out.println(json);

        Object parse = BasicDBObject.parse(json);
        System.out.println(parse);
        Assert.assertEquals(BasicDBObject.class, parse.getClass());
        Assert.assertEquals(Double.class, ((DBObject) parse).get("value").getClass());
        Assert.assertEquals(5.0D, (double) ((DBObject) parse).get("value"), 0);
    }

    protected Mapper createMapper() {
        Builder builder = new JacksonMapper.Builder();
        builder.enable(MapperFeature.AUTO_DETECT_GETTERS);
        builder.addSerializer(DateTime.class, new JodaMapping.MongoDateTimeSerializer());
        builder.addDeserializer(DateTime.class, new JodaMapping.MongoDateTimeDeserializer());
        builder.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        builder.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        return builder.build();
    }

}
