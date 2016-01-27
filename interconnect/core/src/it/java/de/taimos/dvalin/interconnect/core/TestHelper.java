package de.taimos.dvalin.interconnect.core;


import de.taimos.dvalin.interconnect.core.exceptions.InfrastructureException;

public final class TestHelper {

    /**
     * @param url Broker url
     */
    public static void initBrokerEnv(final String url) {
        System.setProperty(MessageConnector.SYSPROP_IBROKERURL, url);
        try {
            MessageConnector.start(url);
        } catch (final InfrastructureException e) {
            throw new RuntimeException(e);
        }
    }

    /** */
    public static void closeBrokerEnv() {
        try {
            MessageConnector.stop();
        } catch (final InfrastructureException e) {
            throw new RuntimeException(e);
        }
    }

    private TestHelper() {
        super();
    }

}
