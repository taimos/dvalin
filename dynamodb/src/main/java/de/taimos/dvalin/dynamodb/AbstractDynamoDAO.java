/*
 * Copyright (c) 2016. Taimos GmbH http://www.taimos.de
 */

package de.taimos.dvalin.dynamodb;

/*-
 * #%L
 * DynamoDB support for dvalin
 * %%
 * Copyright (C) 2016 - 2017 Taimos GmbH
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

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.DescribeTableResult;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;

import de.taimos.dvalin.cloud.aws.AWSClient;

public abstract class AbstractDynamoDAO<T> {

    public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @AWSClient(endpoint = "${dynamodb.url:}")
    protected AmazonDynamoDBClient client;

    protected DynamoDBMapper mapper;

    @PostConstruct
    public final void init() {
        this.mapper = new DynamoDBMapper(this.client);
        try {
            DescribeTableResult tableResult = this.client.describeTable(this.getTableName());
            this.LOGGER.info("DynamoDB table exists: {}", tableResult.getTable());
        } catch (ResourceNotFoundException e) {
            CreateTableRequest request = this.mapper.generateCreateTableRequest(this.getEntityClass());
            this.LOGGER.info("Create DynamoDB table: {}", request);
            request.setProvisionedThroughput(this.getProvisionedThroughput());
            this.modifyCreateTableRequest(request);
            CreateTableResult table = this.client.createTable(request);
            this.LOGGER.info("Created table: {}", table.getTableDescription());
        }
    }

    /**
     * override to adjust crea table requests
     *
     * @param request the request to modify
     */
    protected void modifyCreateTableRequest(CreateTableRequest request) {
        //
    }

    /**
     * the provisioned throughput of newly created tables. Defaults to (1,1)
     *
     * @return provisioned throughput
     */
    protected ProvisionedThroughput getProvisionedThroughput() {
        return new ProvisionedThroughput(1L, 1L);
    }

    /**
     * the class of the base object to use for this DAO
     *
     * @return entity class
     */
    protected abstract Class<T> getEntityClass();

    /**
     * override to use custom table name instead of @{@link DynamoDBTable} annotation
     *
     * @return the name of the table to use
     */
    protected String getTableName() {
        Class<T> entityClass = this.getEntityClass();
        if (!entityClass.isAnnotationPresent(DynamoDBTable.class)) {
            throw new IllegalStateException("Used getTableName on entity without @DynamoDBTable annotation");
        }
        return entityClass.getAnnotation(DynamoDBTable.class).tableName();
    }

}
