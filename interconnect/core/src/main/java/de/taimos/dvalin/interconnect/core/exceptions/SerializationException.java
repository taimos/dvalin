package de.taimos.dvalin.interconnect.core.exceptions;

/**
 * Exception for all problems concerning the serialization or deserialization of Interconnect data.
 */
public class SerializationException extends InfrastructureException {

    /**
     * version UID for serialization
     */
    private static final long serialVersionUID = 1L;


    /**
     * Default constructor.
     */
    public SerializationException() {
        this("A problem occured during the serialization or deserialization of Interconnect data.", null);
    }

    /**
     * Constructor.
     *
     * @param message a description of the problem
     */
    public SerializationException(String message) {
        this(message, null);
    }

    /**
     * Constructor.
     *
     * @param message a description of the problem
     * @param cause   the cause of the problem
     */
    public SerializationException(String message, Throwable cause) {
        super(message, cause);
    }

}
