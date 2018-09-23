package de.taimos.dvalin.jaxrs;

/*
 * #%L
 * Daemon with Spring and CXF
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

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public final class MapperFactory {

    private MapperFactory() {
        //
    }

    public static ObjectMapper createDefault() {
        ObjectMapper m = new ObjectMapper();
        m.registerModule(new JodaModule());
        m.registerModule(new GuavaModule());
        m.setSerializationInclusion(Include.NON_NULL);
        m.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        m.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        m.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        m.enable(MapperFeature.AUTO_DETECT_GETTERS);
        return m;
    }

    public static ObjectMapper createDefaultYaml() {
        YAMLFactory factory = new YAMLFactory();
        factory.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
        factory.enable(YAMLGenerator.Feature.MINIMIZE_QUOTES);
        factory.enable(YAMLGenerator.Feature.SPLIT_LINES);
        factory.enable(YAMLGenerator.Feature.ALWAYS_QUOTE_NUMBERS_AS_STRINGS);
        ObjectMapper m = new ObjectMapper(factory);
        m.registerModule(new JodaModule());
        m.registerModule(new GuavaModule());
        m.setSerializationInclusion(Include.NON_NULL);
        m.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        m.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        m.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        m.enable(MapperFeature.AUTO_DETECT_GETTERS);
        return m;
    }

}
