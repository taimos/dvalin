package de.taimos.dvalin.interconnect.core.daemon;

import javax.jms.Destination;

import de.taimos.dvalin.interconnect.model.InterconnectObject;


public final class DaemonRequest {

    private final String correlationID;

    private final Destination replyTo;

    private final InterconnectObject ico;


    /**
     * @param aCorrelationID Correlation ID
     * @param aReplyTo       Reply to
     * @param anIco          IVO
     */
    public DaemonRequest(final String aCorrelationID, final Destination aReplyTo, final InterconnectObject anIco) {
        super();
        this.correlationID = aCorrelationID;
        this.replyTo = aReplyTo;
        this.ico = anIco;
    }

    /**
     * @return Correlation ID
     */
    public String getCorrelationID() {
        return this.correlationID;
    }

    /**
     * @return Reply to
     */
    public Destination getReplyTo() {
        return this.replyTo;
    }

    /**
     * @return IVO
     */
    public InterconnectObject getInterconnectObject() {
        return this.ico;
    }

}
