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

import java.util.concurrent.TimeUnit;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoDatabase;
import de.taimos.dvalin.mongo.JongoFactory;
import io.mongock.api.config.LegacyMigration;
import io.mongock.driver.mongodb.sync.v4.driver.MongoSync4Driver;
import io.mongock.runner.standalone.MongockStandalone;
import io.mongock.runner.standalone.RunnerStandaloneBuilder;
import org.jongo.Jongo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoDBConfig {

    @Value("${mongock.enabled:false}")
    private boolean mongockEnabled;

    @Value("${mongock.legacyMigration.enabled:true}")
    private boolean mongockLegacyMigrationEnabled;

    @Value("${mongock.legacyMigration.table:dbchangelog}")
    private String mongockLegacyTable;

    @Value("${mongodb.name}")
    private String dbName;

    @Value("${mongock.basepackage:${mongock.basePackage:}}")
    private String basePackage;

    @Bean
    public RunnerStandaloneBuilder mongockRunner(com.mongodb.client.MongoClient mongoClient, Jongo jongo, DB legacyDB) {

        MongoSync4Driver driver = MongoSync4Driver.withDefaultLock(mongoClient, this.dbName);
        driver.setWriteConcern(WriteConcern.MAJORITY.withJournal(true).withWTimeout(1000, TimeUnit.MILLISECONDS));
        driver.setReadConcern(ReadConcern.MAJORITY);
        driver.setReadPreference(ReadPreference.primary());
        driver.disableTransaction();

        RunnerStandaloneBuilder runnerStandaloneBuilder = MongockStandalone.builder().setDriver(driver).setTransactionEnabled(false);
        if (this.basePackage == null || this.basePackage.isEmpty()){
            throw new RuntimeException("LegacyMigration basePackage must be set!");
        }
        runnerStandaloneBuilder.addMigrationScanPackage(this.basePackage);
        if(mongockLegacyMigrationEnabled) {
            LegacyMigration legacyMigration = new LegacyMigration();
            legacyMigration.setOrigin(this.mongockLegacyTable);
            runnerStandaloneBuilder.setLegacyMigration(legacyMigration);
        }
        runnerStandaloneBuilder.addDependency(jongo).addDependency(legacyDB).buildRunner().execute();
        return runnerStandaloneBuilder;
    }

    @Bean
    public MongoDatabase mongoDatabase(MongoClient mongoClient) {
        return mongoClient.getDatabase(this.dbName);
    }

    @Bean
    public Jongo jongo(MongoClient mongoClient) {
        return JongoFactory.createDefault(mongoClient.getDB(this.dbName));
    }

    @Bean
    public DB mongoDB(MongoClient mongoClient){
        return mongoClient.getDB(this.dbName);
    }
}
