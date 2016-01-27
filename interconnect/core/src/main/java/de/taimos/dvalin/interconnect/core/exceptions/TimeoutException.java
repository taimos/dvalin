package de.taimos.dvalin.interconnect.core.exceptions;

public class TimeoutException extends InfrastructureException {

    private static final long serialVersionUID = 1L;


    /**
     * @param timeout the timeout in millis
     */
    public TimeoutException(long timeout) {
        super(String.format("Failed to receive a response within %d milliseconds.", timeout));
    }

}
