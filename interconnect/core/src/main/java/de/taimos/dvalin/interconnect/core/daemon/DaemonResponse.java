package de.taimos.dvalin.interconnect.core.daemon;


import de.taimos.dvalin.interconnect.model.InterconnectObject;

public final class DaemonResponse {

    private final DaemonRequest request;

    private final InterconnectObject response;


    /**
     * @param aRequest  Request
     * @param aResponse Response
     */
    public DaemonResponse(final DaemonRequest aRequest, final InterconnectObject aResponse) {
        super();
        this.request = aRequest;
        this.response = aResponse;
    }

    /**
     * @return Request
     */
    public DaemonRequest getRequest() {
        return this.request;
    }

    /**
     * @return Response
     */
    public InterconnectObject getResponse() {
        return this.response;
    }

}
