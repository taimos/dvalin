package de.taimos.dvalin.mongo.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.taimos.dvalin.mongo.model.TestObject;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
class JacksonConfigTest {

    private final ObjectMapper mapper = JacksonConfig.createObjectMapper();


    @Test
    void testDateMapping() throws JsonProcessingException {
        String json = "{\"$date\":\"2024-04-03T09:09:03.000Z\"}";
        DateTime date = new DateTime(2024, 4, 3, 9, 9, 3, DateTimeZone.UTC);
        String toJson = this.mapper.writeValueAsString(date);
        assertEquals(json, toJson);
    }

    @Test
    void testObjectWithDateMapping() throws JsonProcessingException {
        String json = "{\"clazz\":\"de.taimos.dvalin.mongo.model.TestObject\",\"name\":null,\"value\":null,\"dt\":{\"$date\":\"2024-04-03T09:09:03.000Z\"},\"_id\":{\"$oid\":\"66682ae8161422626090bad3\"}}";
        TestObject to = new TestObject();
        to.setId("66682ae8161422626090bad3");
        to.setDt(new DateTime(2024, 4, 3, 9, 9, 3, DateTimeZone.UTC));
        String toJson = this.mapper.writeValueAsString(to);
        assertEquals(json, toJson);
    }
}