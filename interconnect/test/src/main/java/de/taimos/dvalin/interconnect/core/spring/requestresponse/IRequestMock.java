package de.taimos.dvalin.interconnect.core.spring.requestresponse;

import java.util.UUID;

import de.taimos.dvalin.interconnect.core.daemon.IDaemonRequestResponse;
import de.taimos.dvalin.interconnect.model.InterconnectObject;
import de.taimos.dvalin.interconnect.model.service.DaemonError;


/**
 * Must be implemented for mocking interconnect communication via {@link IDaemonRequestResponse}.
 */
public interface IRequestMock {

    /**
     * @param <R>           Response type
     * @param uuid          Universally unique identifier of the request
     * @param queue         Queue name
     * @param request       Request
     * @param responseClazz Response class
     * @return Response
     * @throws DaemonError If something went wrong
     */
    <R> R in(UUID uuid, String queue, InterconnectObject request, Class<R> responseClazz) throws DaemonError;

    /**
     * @param uuid    Universally unique identifier of the request
     * @param queue   Queue name
     * @param request Request
     */
    void in(UUID uuid, String queue, InterconnectObject request);
}
