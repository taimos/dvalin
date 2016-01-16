package de.taimos.dvalin.interconnect.core.spring.message;

import de.taimos.dvalin.interconnect.core.spring.DaemonMessageSenderHeader;
import de.taimos.dvalin.interconnect.core.spring.IDaemonMessageSender;
import de.taimos.dvalin.interconnect.model.InterconnectObject;

/**
 * Must be implemented for mocking interconnect communication via {@link IDaemonMessageSender}.
 */
public interface IMessageMock {

    /**
     * @param topic   Topic name
     * @param ico     InterconnectObject
     * @param headers Headers (optional)
     * @throws Exception If something went wrong
     */
    void sendToTopic(String topic, InterconnectObject ico, DaemonMessageSenderHeader... headers) throws Exception;

    /**
     * @param queue         Queue name
     * @param ico           InterconnectObject
     * @param correlationId Correlation id
     * @param headers       Headers (optional)
     * @throws Exception If something went wrong
     */
    void sendToQueue(String queue, InterconnectObject ico, String correlationId, DaemonMessageSenderHeader... headers) throws Exception;

}
