package de.taimos.dvalin.interconnect.core.exceptions;

/**
 * Exception for all cases in which data cannot be accessed or found.
 */
public class AccessException extends Exception {

    /**
     * version UID for serialization
     */
    private static final long serialVersionUID = 1L;


    /**
     * Default constructor.
     */
    public AccessException() {
        this("Some data could not be accessed or found.", null);
    }

    /**
     * Constructor.
     *
     * @param message a description of the problem
     */
    public AccessException(String message) {
        this(message, null);
    }

    /**
     * Constructor.
     *
     * @param message a description of the problem
     * @param cause   the cause of the problem
     */
    public AccessException(String message, Throwable cause) {
        super(message, cause);
    }

}
