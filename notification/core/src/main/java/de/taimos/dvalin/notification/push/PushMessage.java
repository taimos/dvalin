/*
 * Copyright (c) 2016. Taimos GmbH
 *
 */

package de.taimos.dvalin.notification.push;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public abstract class PushMessage {
    
    private static ObjectMapper MAPPER;
    
    static {
        MAPPER = new ObjectMapper();
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MAPPER.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        MAPPER.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        MAPPER.enable(MapperFeature.AUTO_DETECT_GETTERS);
    }
    
    public abstract Platform getType();
    
    protected abstract Map<String, Object> getPayload();
    
    public String getPushMessage() throws IOException {
        return PushMessage.MAPPER.writeValueAsString(this.getPayload());
    }
}
