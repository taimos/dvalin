package de.taimos.dvalin.mongo.mapper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
public class JacksonConfig {
    /**
     * @return configured Jackson object mapper
     */
    public static ObjectMapper createObjectMapper() {
        return JsonMapper.builder() //
            .enable(MapperFeature.AUTO_DETECT_GETTERS) //
            // enable SerializationFeatures
            .enable(SerializationFeature.FAIL_ON_EMPTY_BEANS)//
            // enable DeserializationFeature
            .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)//
            // disable SerializationFeature
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)//
            // disable DeserializationFeature
            .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)//
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)//
            // modules
            .addModule(new DvalinJodaModule()).build();
    }
}
