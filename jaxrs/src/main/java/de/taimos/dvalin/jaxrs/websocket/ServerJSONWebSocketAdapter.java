package de.taimos.dvalin.jaxrs.websocket;

/*
 * #%L
 * JAX-RS support for dvalin using Apache CXF
 * %%
 * Copyright (C) 2015 - 2016 Taimos GmbH
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

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.api.WriteCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.taimos.dvalin.jaxrs.MapperFactory;

/**
 * Adapter for server-side websocket sessions using JSON transport
 *
 * @param <T>
 * @author thoeger
 */
public abstract class ServerJSONWebSocketAdapter<T> extends WebSocketAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerJSONWebSocketAdapter.class);

    private final ObjectMapper mapper = MapperFactory.createDefault();

    @Override
    public final void onWebSocketText(String message) {
        if ((message == null) || message.isEmpty()) {
            ServerJSONWebSocketAdapter.LOGGER.info("Got empty socket data");
            this.onWebSocketEmptyMessage();
            return;
        }
        try {
            T msg = this.mapper.readValue(message, this.getObjectType());
            this.onWebSocketObject(msg);
        } catch (IOException e1) {
            ServerJSONWebSocketAdapter.LOGGER.info("Got invalid message", e1);
            this.onWebSocketInvalidMessage(message);
            return;
        }
    }

    /**
     * called if received message was null or empty
     */
    protected void onWebSocketEmptyMessage() {
        //
    }

    /**
     * called if message was invalid and not parsable into the given type
     *
     * @param message the raw message received on the socket
     */
    @SuppressWarnings("unused")
    protected void onWebSocketInvalidMessage(String message) {
        //
    }

    /**
     * @return the object type for incoming messages
     */
    protected abstract Class<T> getObjectType();

    /**
     * called on incoming messages that were parsed into the given object class
     *
     * @param message the parsed an converted message
     */
    protected abstract void onWebSocketObject(T message);

    /**
     * convert object into another class using the JSON mapper
     *
     * @param <C>         the generic target type
     * @param object      the object to convert
     * @param targetClass the class of the target object
     * @return the converted object
     * @throws IllegalArgumentException if conversion fails
     */
    protected <C> C convert(Object object, Class<C> targetClass) {
        return this.mapper.convertValue(object, targetClass);
    }

    /**
     * send object to client and serialize it using JSON<br>
     * uses a generic callback that prints errors to the log
     *
     * @param objectToSend the object to send
     */
    protected final void sendObjectToSocket(final Object objectToSend) {
        this.sendObjectToSocket(objectToSend, new WriteCallback() {

            @Override
            public void writeSuccess() {
                ServerJSONWebSocketAdapter.LOGGER.debug("Send data to socket: {}", objectToSend);
            }

            @Override
            public void writeFailed(Throwable x) {
                ServerJSONWebSocketAdapter.LOGGER.error("Error sending message to socket", x);
            }
        });
    }

    /**
     * send object to client and serialize it using JSON
     *
     * @param objectToSend  the object to send
     * @param cb the callback after sending the message
     */
    protected final void sendObjectToSocket(Object objectToSend, WriteCallback cb) {
        Session sess = this.getSession();
        if (sess != null) {
            String json;
            try {
                json = this.mapper.writeValueAsString(objectToSend);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize object", e);
            }
            sess.getRemote().sendString(json, cb);
        }
    }

}
