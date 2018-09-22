package de.taimos.dvalin.mongo.links;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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

    private final Jongo jongo;

    @Autowired
    public DLinkDAO(Jongo jongo) {
        this.jongo = jongo;
    }

    @Override
    public <T extends AReferenceableEntity<T>> T resolve(DocumentLink<T> link) {
        MongoCollection collection = this.jongo.getCollection(link.getTargetClass().getSimpleName());
        return collection.findOne(new ObjectId(link.getObjectId())).as(link.getTargetClass());
    }

    @Override
    public <T extends AReferenceableEntity<T>> List<T> resolve(List<DocumentLink<T>> links, Class<T> targetClass) {
        MongoCollection collection = this.jongo.getCollection(targetClass.getSimpleName());
        List<ObjectId> ids = new ArrayList<>();
        for (DocumentLink<T> link : links) {
            if (!link.getTargetClass().equals(targetClass)) {
                throw new IllegalArgumentException("Invalid link in collection");
            }
            ids.add(new ObjectId(link.getObjectId()));
        }
        Iterator<T> it = collection.find("{\"_id\" : {\"$in\" : #}}", ids).as(targetClass).iterator();
        List<T> resolved = new ArrayList<>();
        while (it.hasNext()) {
            resolved.add(it.next());
        }
        return resolved;
    }

}
