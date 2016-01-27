package de.taimos.dvalin.interconnect.core.exceptions;

public class VersionMismatchException extends InfrastructureException {

    private static final long serialVersionUID = 1L;


    /**
     * @param expected the expected version
     * @param actual   the actual version received
     */
    public VersionMismatchException(String expected, String actual) {
        super(String.format("Interconnect version mismatch! Expected version %s and received version %s", expected, actual));
    }

}
