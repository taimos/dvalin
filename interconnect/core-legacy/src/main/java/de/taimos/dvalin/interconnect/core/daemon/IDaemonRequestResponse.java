package de.taimos.dvalin.interconnect.core.daemon;

/*
 * #%L
 * Dvalin interconnect core library
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

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import de.taimos.dvalin.interconnect.model.InterconnectObject;


/**
 * This interface provides a way to send a request and get a response.
 */
public interface IDaemonRequestResponse {

    /**
     * @param uuid          Universally unique identifier of the request
     * @param queue         Queue name
     * @param request       Request IVO
     * @param responseClazz the response class
     * @param <R>           Response type
     * @return Response IVO
     * @throws ExecutionException If the requests fails
     */
    <R> R sync(UUID uuid, String queue, InterconnectObject request, Class<R> responseClazz) throws ExecutionException;

    /**
     * @param uuid          Universally unique identifier of the request
     * @param queue         Queue name
     * @param request       Request IVO
     * @param responseClazz the response class
     * @param timeout       maximum time to wait
     * @param unit          time unit of the timeout argument
     * @param <R>           Response type
     * @return Response IVO
     * @throws ExecutionException If the requests fails
     */
    <R> R sync(UUID uuid, String queue, InterconnectObject request, Class<R> responseClazz, long timeout, TimeUnit unit) throws ExecutionException;

    /**
     * @param uuid          Universally unique identifier of the request
     * @param queue         Queue name
     * @param request       Request IVO
     * @param responseClazz the response class
     * @param timeout       maximum time to wait
     * @param unit          time unit of the timeout argument
     * @param secure        Secure (encrypted communication)
     * @param <R>           Response type
     * @return Response IVO
     * @throws ExecutionException If the requests fails
     */
    <R> R sync(UUID uuid, String queue, InterconnectObject request, Class<R> responseClazz, long timeout, TimeUnit unit, boolean secure) throws ExecutionException;

    /**
     * @param uuid          Universally unique identifier of the request
     * @param queue         Queue name
     * @param request       Request IVO
     * @param responseClazz the response class
     * @param <R>           Response type
     * @return Response IVO
     */
    <R> Future<R> async(UUID uuid, String queue, InterconnectObject request, Class<R> responseClazz);

    /**
     * @param uuid          Universally unique identifier of the request
     * @param queue         Queue name
     * @param request       Request IVO
     * @param responseClazz the response class
     * @param timeout       maximum time to wait
     * @param unit          time unit of the timeout argument
     * @param <R>           Response type
     * @return Response IVO
     */
    <R> Future<R> async(UUID uuid, String queue, InterconnectObject request, Class<R> responseClazz, long timeout, TimeUnit unit);

    /**
     * @param uuid          Universally unique identifier of the request
     * @param queue         Queue name
     * @param request       Request IVO
     * @param responseClazz the response class
     * @param timeout       maximum time to wait
     * @param unit          time unit of the timeout argument
     * @param secure        Secure (encrypted communication)
     * @param <R>           Response type
     * @return Response IVO
     */
    <R> Future<R> async(UUID uuid, String queue, InterconnectObject request, Class<R> responseClazz, long timeout, TimeUnit unit, boolean secure);

}
