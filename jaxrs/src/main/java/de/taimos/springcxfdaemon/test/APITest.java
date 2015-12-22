package de.taimos.springcxfdaemon.test;

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
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.http.HttpResponse;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.taimos.daemon.spring.SpringDaemonTestRunner;
import de.taimos.httputils.HTTPRequest;
import de.taimos.httputils.WS;
import de.taimos.springcxfdaemon.MapperFactory;
import de.taimos.springcxfdaemon.websocket.ClientSocketAdapter;

@RunWith(SpringDaemonTestRunner.class)
public abstract class APITest {
	
	private static final String APPLICATION_JSON = "application/json";
	
	@Value("${server.url:http://localhost:${jaxrs.bindport:${svc.port:8080}}}")
	private String serverURL;
	@Value("${server.wsurl:ws://localhost:${jaxrs.bindport:${svc.port:8080}}}")
	private String websocketURL;
	
	
	// Getter methods for server configuration
	
	/**
	 * @return the URL of the test server
	 */
	protected final String getServerURL() {
		return this.serverURL;
	}
	
	/**
	 * @return the URL of the websocket endpoint of the test server
	 */
	protected String getWebSocketURL() {
		return this.websocketURL;
	}
	
	/**
	 * Create new HTTP request to the test server
	 * 
	 * @param path the path to call
	 * @return the created {@link HTTPRequest}
	 */
	protected final HTTPRequest request(String path) {
		return WS.url(this.serverURL + path);
	}
	
	/**
	 * assert that the response has a 2XX status code
	 * 
	 * @param res the response to check
	 */
	protected final void assertOK(HttpResponse res) {
		Assert.assertTrue(String.format("Expected OK - was %s", WS.getStatus(res)), WS.isStatusOK(res));
	}
	
	/**
	 * assert that the response has the given status code
	 * 
	 * @param res the response to check
	 * @param status the status to check against
	 */
	protected final void assertStatus(HttpResponse res, Status status) {
		Assert.assertTrue(String.format("Expected %s - was %s", status.getStatusCode(), WS.getStatus(res)), WS.getStatus(res) == status.getStatusCode());
	}
	
	/**
	 * assert that the response has a 2XX status code
	 * 
	 * @param res the response to check
	 */
	protected final void assertOK(Response res) {
		Assert.assertTrue(String.format("Expected OK - was %s", res.getStatus()), (res.getStatus() >= 200) && (res.getStatus() <= 299));
	}
	
	/**
	 * assert that the response has the given status code
	 * 
	 * @param res the response to check
	 * @param status the status to check against
	 */
	protected final void assertStatus(Response res, Status status) {
		Assert.assertTrue(String.format("Expected %s - was %s", status.getStatusCode(), res.getStatus()), res.getStatus() == status.getStatusCode());
	}
	
	/**
	 * reads the response to the given object using the default JSON ObjectMapper
	 * 
	 * @param <T> the target class
	 * @param res the response to convert
	 * @param clazz the class of the target
	 * @return the converted object
	 * @throws RuntimeException if deserialization fails
	 */
	protected <T> T read(HttpResponse res, Class<T> clazz) {
		try {
			return MapperFactory.createDefault().readValue(res.getEntity().getContent(), clazz);
		} catch (IllegalStateException | IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * reads the response into a map of type string-object using the default JSON ObjectMapper
	 * 
	 * @param res the response to convert
	 * @return the converted map
	 * @throws RuntimeException if deserialization fails
	 */
	@SuppressWarnings("unchecked")
	protected Map<String, Object> readMap(HttpResponse res) {
		return this.read(res, Map.class);
	}
	
	/**
	 * enriches the given {@link HTTPRequest} with the the given object as JSON using the default ObjectMapper
	 * 
	 * @param req the request to enrich
	 * @param o the object to map and use as body
	 * @return the enriched request
	 * @throws RuntimeException if serialization fails
	 */
	protected HTTPRequest jsonBody(HTTPRequest req, Object o) {
		try {
			String json = MapperFactory.createDefault().writeValueAsString(o);
			return req.contentType(APITest.APPLICATION_JSON).body(json);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * creates a web socket connection to the given path using the supplied client socket adapter
	 * 
	 * @param path the path of the websocket target
	 * @param socket the client socket to use
	 * @return the created {@link WebSocketClient}
	 * @throws RuntimeException if connection fails
	 */
	protected WebSocketClient openWebsocket(String path, ClientSocketAdapter socket) {
		try {
			WebSocketClient cl = new WebSocketClient();
			cl.start();
			ClientUpgradeRequest request = new ClientUpgradeRequest();
			socket.modifyRequest(request);
			Future<Session> socketSession = cl.connect(socket, URI.create(this.getWebSocketURL() + path), request);
			socketSession.get(5, TimeUnit.SECONDS);
			return cl;
		} catch (Exception e) {
			throw new RuntimeException("WebSocket failed", e);
		}
	}
	
	/**
	 * prints the given object serialized to JSON to the console. The given label is prefixed to the output
	 * 
	 * @param label the label to use as prefix
	 * @param o the object to serialize
	 * @throws RuntimeException if serialization fails
	 */
	protected void print(String label, Object o) {
		try {
			String json = MapperFactory.createDefault().writeValueAsString(o);
			System.out.println(label + ": " + json);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
	
}
