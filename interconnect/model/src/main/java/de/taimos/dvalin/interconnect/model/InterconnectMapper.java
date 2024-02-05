package de.taimos.dvalin.interconnect.model;

/*
 * #%L
 * Dvalin interconnect transfer data model
 * %%
 * Copyright (C) 2016 Taimos GmbH
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

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.joda.time.DateTime;

/**
 * Utility class mapping JSON data to {@link InterconnectObject} and vice versa.
 */
public final class InterconnectMapper {

    /**
     * JSON mapper.
     */
    private static final ObjectMapper mapper = InterconnectMapper.createMapper();


    /**
     * Class constructor.
     */
    private InterconnectMapper() {
        // This is a utility class. Do not instantiate.
    }

    /**
     * Creates an {@link InterconnectObject} from the given JSON data.
     *
     * @param data the JSON data
     * @return the object contained in the given JSON data
     * @throws JsonParseException   if a the JSON data could not be parsed
     * @throws JsonMappingException if the mapping of the JSON data to the IVO failed
     * @throws IOException          if an I/O related problem occurred
     */
    public static InterconnectObject fromJson(String data) throws IOException {
        return InterconnectMapper.fromJson(data, InterconnectObject.class);
    }

    /**
     * Creates an object from the given JSON data.
     *
     * @param data  the JSON data
     * @param clazz the class object for the content of the JSON data
     * @param <T>   the type of the class object extending {@link InterconnectObject}
     * @return the object contained in the given JSON data
     * @throws JsonParseException   if a the JSON data could not be parsed
     * @throws JsonMappingException if the mapping of the JSON data to the IVO failed
     * @throws IOException          if an I/O related problem occurred
     */
    public static <T extends InterconnectObject> T fromJson(String data, Class<T> clazz) throws IOException {
        return InterconnectMapper.mapper.readValue(data, clazz);
    }

    /**
     * Returns a JSON representation of the given object.
     *
     * @param object the object to be stored in a JSON string
     * @return a JSON representation of this IVO.
     * @throws JsonGenerationException if the JSON data could not be generated
     * @throws JsonMappingException    if the object could not be mapped to a JSON string
     * @throws IOException             if an I/O related problem occurred
     */
    public static String toJson(InterconnectObject object) throws IOException {
        return InterconnectMapper.mapper.writeValueAsString(object);
    }

    /**
     * Returns a clone of the given object using JSON (de)serialization.
     *
     * @param object the object to be cloned
     * @param <T> the cloned object class name
     * @return a clone of this object.
     * @throws JsonGenerationException if the JSON data could not be generated
     * @throws JsonMappingException    if the object could not be mapped to a JSON string
     * @throws IOException             if an I/O related problem occurred
     */
    public static <T extends InterconnectObject> T cloneObject(T object) throws IOException {
        return InterconnectMapper.fromJson(InterconnectMapper.toJson(object), (Class<T>) object.getClass());
    }

    private static ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();
        JodaModule jm = new JodaModule();
        // make some modifications to ensure correct tz serialization and get map keys working
        jm.addKeyDeserializer(DateTime.class, new DateTimeKeyDeserializer());
        jm.addDeserializer(DateTime.class, new DateTimeDeserializerWithTZ());
        mapper.registerModule(jm);
        mapper.registerModule(new GuavaModule());
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        return mapper;
    }

    /**
     * Allows registering modules at ObjectMapper
     *
     * @param module module to register
     */
    public static void registerModule(Module module) {
        InterconnectMapper.mapper.registerModule(module);
    }

    /**
     * Allows enabling feature at ObjectMapper
     *
     * @param feature feature to enable
     */
    public static void enableFeature(DeserializationFeature feature) {
        InterconnectMapper.mapper.enable(feature);
    }

    /**
     * Allows disabling feature at ObjectMapper
     *
     * @param feature feature to disable
     */
    public static void disableFeature(SerializationFeature feature) {
        InterconnectMapper.mapper.disable(feature);
    }

}
