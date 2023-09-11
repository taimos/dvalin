package de.taimos.dvalin.jaxrs.providers;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import de.taimos.dvalin.jaxrs.MapperFactory;

import jakarta.annotation.Priority;
import javax.ws.rs.Consumes;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Priorities;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Priority(Priorities.ENTITY_CODER)
@Consumes(MediaType.WILDCARD)
@Produces(MediaType.WILDCARD)
public class JacksonProvider implements MessageBodyReader<Object>, MessageBodyWriter<Object> {

    protected final ObjectMapper jsonMapper;
    protected final ObjectMapper yamlMapper;

    /**
     * Create default JacksonProvider
     */
    public JacksonProvider() {
        this(MapperFactory.createDefault(), MapperFactory.createDefaultYaml());
    }

    /**
     * Create JacksonProvider with custom ObjectMappers
     *
     * @param jsonMapper custom mapper for JSON
     * @param yamlMapper custom mapper for YAML
     */
    public JacksonProvider(ObjectMapper jsonMapper, ObjectMapper yamlMapper) {
        this.jsonMapper = jsonMapper;
        this.yamlMapper = yamlMapper;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return this.isJson(mediaType) || this.isYaml(mediaType);
    }

    @Override
    public long getSize(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        if (this.isYaml(mediaType)) {
            entityStream.write(this.yamlMapper.writeValueAsBytes(t));
        } else if (this.isJson(mediaType)) {
            entityStream.write(this.jsonMapper.writeValueAsBytes(t));
        } else {
            throw new InternalServerErrorException("Mapping error");
        }
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return this.isJson(mediaType) || this.isYaml(mediaType);
    }

    @Override
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        final ObjectMapper mapper;
        if (this.isYaml(mediaType)) {
            mapper = this.yamlMapper;
        } else if (this.isJson(mediaType)) {
            mapper = this.jsonMapper;
        } else {
            throw new InternalServerErrorException("Mapping error");
        }

        if (genericType == null) {
            return mapper.readValue(entityStream, type);
        }
        return mapper.readValue(entityStream, TypeFactory.defaultInstance().constructType(genericType));
    }

    protected boolean isYaml(MediaType mediaType) {
        return mediaType.getSubtype().equals("yaml") || mediaType.getSubtype().endsWith("+yaml");
    }

    protected boolean isJson(MediaType mediaType) {
        return mediaType.getSubtype().equals("json") || mediaType.getSubtype().endsWith("+json");
    }

}
