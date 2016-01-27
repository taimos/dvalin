package de.taimos.dvalin.interconnect.core.spring;

import javax.jms.Destination;

import de.taimos.dvalin.interconnect.core.daemon.DaemonResponse;
import de.taimos.dvalin.interconnect.model.InterconnectObject;


/**
 * Send a response.
 */
public interface IDaemonMessageSender {

    /**
     * Reply with a Daemon response.
     *
     * @param response Response
     * @throws Exception If something went wrong
     */
    void reply(DaemonResponse response) throws Exception;

    /**
     * Reply with a Daemon response.
     *
     * @param response Response
     * @param secure   (encrypted communication)
     * @throws Exception If something went wrong
     */
    void reply(DaemonResponse response, boolean secure) throws Exception;

    /**
     * Reply with an IVO.
     *
     * @param correlationID Correlation ID
     * @param replyTo       Reply to
     * @param ico           InterconnectObject
     * @throws Exception If something went wrong
     */
    void reply(String correlationID, Destination replyTo, InterconnectObject ico) throws Exception;

    /**
     * Reply with an IVO.
     *
     * @param correlationID Correlation ID
     * @param replyTo       Reply to
     * @param ico           InterconnectObject
     * @param secure        (encrypted communication)
     * @throws Exception If something went wrong
     */
    void reply(String correlationID, Destination replyTo, InterconnectObject ico, boolean secure) throws Exception;

    /**
     * @param topic   Topic name
     * @param ico     InterconnectObject
     * @param headers Headers (optional)
     * @throws Exception If something went wrong
     */
    void sendToTopic(String topic, InterconnectObject ico, DaemonMessageSenderHeader... headers) throws Exception;

    /**
     * @param queue   Queue name
     * @param ico     InterconnectObject
     * @param headers Headers (optional)
     * @throws Exception If something went wrong
     */
    void sendToQueue(String queue, InterconnectObject ico, DaemonMessageSenderHeader... headers) throws Exception;

    /**
     * @param topic   Topic name
     * @param ico     InterconnectObject
     * @param secure  (encrypted communication)
     * @param headers Headers (optional)
     * @throws Exception If something went wrong
     */
    void sendToTopic(String topic, InterconnectObject ico, boolean secure, DaemonMessageSenderHeader... headers) throws Exception;

    /**
     * @param queue   Queue name
     * @param ico     InterconnectObject
     * @param secure  (encrypted communication)
     * @param headers Headers (optional)
     * @throws Exception If something went wrong
     */
    void sendToQueue(String queue, InterconnectObject ico, boolean secure, DaemonMessageSenderHeader... headers) throws Exception;

    /**
     * @param queue         Queue name
     * @param ico           InterconnectObject
     * @param secure        (encrypted communication)
     * @param replyToQueue  Reply To queue (can be null)
     * @param correlationId Correlation id (can be null)
     * @param headers       Headers (optional)
     * @throws Exception If something went wrong
     */
    void sendToQueue(String queue, InterconnectObject ico, boolean secure, String replyToQueue, String correlationId, DaemonMessageSenderHeader... headers) throws Exception;

}
