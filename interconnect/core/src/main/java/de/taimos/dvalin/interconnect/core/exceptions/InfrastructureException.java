package de.taimos.dvalin.interconnect.core.exceptions;

/**
 * Exception for all problems concerning the Interconnect infrastructure.
 */
public class InfrastructureException extends Exception {

    /**
     * version UID for serialization
     */
    private static final long serialVersionUID = 1L;


    /**
     * Default constructor.
     */
    public InfrastructureException() {
        this("A problem with the Interconnect infrastructure occured", null);
    }

    /**
     * Constructor.
     *
     * @param message a description of the problem
     */
    public InfrastructureException(String message) {
        this(message, null);
    }

    /**
     * Constructor.
     *
     * @param message a description of the problem
     * @param cause   the cause of the problem
     */
    public InfrastructureException(String message, Throwable cause) {
        super(message, cause);
    }

}
