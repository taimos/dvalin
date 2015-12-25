package de.taimos.dvalin.mongo;

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

import org.joda.time.DateTime;
import org.jongo.Jongo;
import org.jongo.marshall.jackson.JacksonMapper;
import org.jongo.marshall.jackson.JacksonMapper.Builder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mongodb.DB;

/**
 * Copyright 2015 Hoegernet<br>
 * <br>
 * factory creating the JacksonMapper used by Jongo driver
 *
 * @author Thorsten Hoeger
 */
public final class JongoFactory {

    private JongoFactory() {
        // private utility class constructor
    }

    public static Jongo createDefault(DB db) {
        Builder builder = new JacksonMapper.Builder();
        builder.enable(MapperFeature.AUTO_DETECT_GETTERS);
        builder.addSerializer(DateTime.class, new JodaMapping.MongoDateTimeSerializer());
        builder.addDeserializer(DateTime.class, new JodaMapping.MongoDateTimeDeserializer());
        builder.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        builder.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        return new Jongo(db, builder.build());
    }

}
