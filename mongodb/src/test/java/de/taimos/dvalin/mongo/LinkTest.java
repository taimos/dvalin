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

import java.lang.reflect.Field;
import java.util.List;

import de.taimos.dvalin.daemon.spring.InjectionUtils;
import de.taimos.dvalin.mongo.links.DLinkDAO;
import de.taimos.dvalin.mongo.model.LinkObject;
import de.taimos.dvalin.mongo.model.LinkedObject;
import de.taimos.dvalin.mongo.model.TestObject;
import io.mongock.driver.mongodb.sync.v4.driver.MongoSync4Driver;
import io.mongock.runner.standalone.MongockStandalone;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Copyright 2015 Taimos GmbH<br>
 * <br>
 *
 * @author thoeger
 */
public class LinkTest {

    private static final LinkDAO dao = new LinkDAO();
    private static final LinkedDAO ldao = new LinkedDAO();

    private static final DLinkDAO dlinkDAO = new DLinkDAO(ABaseTest.mongo.getDatabase(ABaseTest.dbName));


    @BeforeAll
    public static void init() {
        try {
            System.setProperty("mongodb.name", ABaseTest.dbName);

            Field dao2Field = AbstractMongoDAO.class.getDeclaredField("dataAccess");
            dao2Field.setAccessible(true);
            dao2Field.set(LinkTest.dao, new MongoDBDataAccess<TestObject>(ABaseTest.database, InjectionUtils.createDependencyDescriptor(dao2Field, LinkTest.dao)));
            dao2Field.set(LinkTest.ldao, new MongoDBDataAccess<TestObject>(ABaseTest.database, InjectionUtils.createDependencyDescriptor(dao2Field, LinkTest.ldao)));


            MongoSync4Driver driver = MongoSync4Driver.withDefaultLock(ABaseTest.mongo, ABaseTest.dbName);
            driver.disableTransaction();
            MongockStandalone.builder().setDriver(driver).addMigrationScanPackage("de.taimos.dvalin.mongo.changelog").setTransactionEnabled(false).setEnabled(true).buildRunner().execute();
            LinkTest.dao.init();
            LinkTest.ldao.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testLinks() {
        LinkedObject lo1 = new LinkedObject();
        lo1.setName("LinkedObject1");
        lo1 = LinkTest.ldao.save(lo1);

        LinkedObject lo2 = new LinkedObject();
        lo2.setName("LinkedObject2");
        lo2 = LinkTest.ldao.save(lo2);

        LinkObject lo = new LinkObject();
        lo.setName("LinkObject");
        lo.getLinks().add(lo1.asLink());
        lo.getLinks().add(lo2.asLink());
        lo = LinkTest.dao.save(lo);

        Assertions.assertEquals(2, lo.getLinks().size());

        List<LinkedObject> list = LinkTest.dlinkDAO.resolve(lo.getLinks(), LinkedObject.class);
        Assertions.assertEquals(2, list.size());
    }

}
