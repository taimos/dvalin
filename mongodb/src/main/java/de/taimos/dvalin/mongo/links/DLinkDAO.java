package de.taimos.dvalin.mongo.links;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import de.taimos.dvalin.mongo.mapper.JacksonConfig;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

/*
 * #%L
 * Spring DAO Mongo
 * %%
 * Copyright (C) 2013 - 2015 Taimos GmbH
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


@Repository
public class DLinkDAO implements IDLinkDAO {

    private final MongoDatabase mongoDatabase;
    private ObjectMapper mapper;

    @Autowired
    public DLinkDAO(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
        this.initMapper();
    }

    protected void initMapper() {
        this.mapper = JacksonConfig.createObjectMapper();
    }

    @Override
    public <T extends AReferenceableEntity<T>> T resolve(DocumentLink<T> link) {
        MongoCollection<Document> collection = this.mongoDatabase.getCollection(link.getTargetClass().getSimpleName());
        return collection.find(eq(new ObjectId(link.getObjectId())))
            .map(doc -> this.mapToEntity(doc, link.getTargetClass())).first();
    }

    @Override
    public <T extends AReferenceableEntity<T>> List<T> resolve(List<DocumentLink<T>> links, Class<T> targetClass) {
        MongoCollection<Document> collection = this.mongoDatabase.getCollection(targetClass.getSimpleName());

        List<ObjectId> ids = new ArrayList<>();
        for (DocumentLink<T> link : links) {
            if (!link.getTargetClass().equals(targetClass)) {
                throw new IllegalArgumentException("Invalid link in collection");
            }
            ids.add(new ObjectId(link.getObjectId()));
        }
        return collection.find(Filters.in("_id", ids)).map(doc -> this.mapToEntity(doc, targetClass))
            .into(new ArrayList<>());
    }


    protected <T extends AReferenceableEntity<T>> T mapToEntity(Document document, Class<T> targetClass) {
        try {
            return this.mapper.readValue(document.toJson(), targetClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
