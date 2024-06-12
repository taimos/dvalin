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

import com.mongodb.MongoClientSettings;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import de.taimos.dvalin.mongo.JodaCodec;
import io.mongock.api.config.LegacyMigration;
import io.mongock.driver.mongodb.sync.v4.driver.MongoSync4Driver;
import io.mongock.runner.standalone.MongockStandalone;
import io.mongock.runner.standalone.RunnerStandaloneBuilder;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

import static org.bson.codecs.pojo.Conventions.DEFAULT_CONVENTIONS;

/**
 * Configuration of the mongo db
 *
 * @author fzwirn
 */
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

    /**
     * @param mongoClient to be used
     * @return a configured mongo database
     */
    @Bean
    public MongoDatabase mongoDatabase(MongoClient mongoClient) {
        return mongoClient.getDatabase(this.dbName) //
            .withCodecRegistry(CodecRegistries.fromRegistries( //
                MongoClientSettings.getDefaultCodecRegistry(), //
                CodecRegistries.fromCodecs(new JodaCodec()), //
                CodecRegistries.fromProviders(MongoDBConfig.getPojoCodecProvider()) //
            ));
    }

    /**
     * Will create and return a {@link RunnerStandaloneBuilder}.
     * <p>
     * Within the bean creation the builder is also constructed and executed.
     *
     * @param mongoClient   to be used
     * @param mongoDatabase to be used
     * @return mongock standalone builder
     */
    @Bean
    public RunnerStandaloneBuilder mongockRunner(MongoClient mongoClient, MongoDatabase mongoDatabase) {
        if (this.basePackage == null || this.basePackage.isEmpty()) {
            throw new RuntimeException("LegacyMigration basePackage must be set!");
        }

        RunnerStandaloneBuilder runnerStandaloneBuilder = MongockStandalone.builder() //
            .setDriver(this.createMongoSyncDriver(mongoClient)) //
            .setEnabled(this.mongockEnabled) //
            .setTransactionEnabled(false) //
            .addMigrationScanPackage(this.basePackage)
            .setLegacyMigration(this.mongockLegacyMigrationEnabled ? this.getLegacyMigration() : null) //
            .addDependency(mongoDatabase);

        runnerStandaloneBuilder.buildRunner().execute();

        return runnerStandaloneBuilder;
    }

    private MongoSync4Driver createMongoSyncDriver(MongoClient mongoClient) {
        MongoSync4Driver driver = MongoSync4Driver.withDefaultLock(mongoClient, this.dbName);
        driver.setWriteConcern(WriteConcern.MAJORITY.withJournal(true).withWTimeout(1000, TimeUnit.MILLISECONDS));
        driver.setReadConcern(ReadConcern.MAJORITY);
        driver.setReadPreference(ReadPreference.primary());
        driver.disableTransaction();
        return driver;
    }

    private LegacyMigration getLegacyMigration() {
        LegacyMigration legacyMigration = new LegacyMigration();
        legacyMigration.setOrigin(this.mongockLegacyTable);
        return legacyMigration;
    }


    private static PojoCodecProvider getPojoCodecProvider() {
        return PojoCodecProvider.builder().conventions(DEFAULT_CONVENTIONS).automatic(true).build();
    }
}
