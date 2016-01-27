package de.taimos.dvalin.interconnect.core.daemon;

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
     * @param responseClazz
     * @param <R>           Response type
     * @return Response IVO
     * @throws ExecutionException If the requests fails
     */
    <R> R sync(UUID uuid, String queue, InterconnectObject request, Class<R> responseClazz) throws ExecutionException;

    /**
     * @param uuid          Universally unique identifier of the request
     * @param queue         Queue name
     * @param request       Request IVO
     * @param responseClazz
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
     * @param responseClazz
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
     * @param responseClazz
     * @param <R>           Response type
     * @return Response IVO
     */
    <R> Future<R> async(UUID uuid, String queue, InterconnectObject request, Class<R> responseClazz);

    /**
     * @param uuid          Universally unique identifier of the request
     * @param queue         Queue name
     * @param request       Request IVO
     * @param responseClazz
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
     * @param responseClazz
     * @param timeout       maximum time to wait
     * @param unit          time unit of the timeout argument
     * @param secure        Secure (encrypted communication)
     * @param <R>           Response type
     * @return Response IVO
     */
    <R> Future<R> async(UUID uuid, String queue, InterconnectObject request, Class<R> responseClazz, long timeout, TimeUnit unit, boolean secure);

}
