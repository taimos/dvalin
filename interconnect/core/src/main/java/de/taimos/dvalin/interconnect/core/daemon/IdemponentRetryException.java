package de.taimos.dvalin.interconnect.core.daemon;

/**
 * Forces a idempotent method call to be retried because of an exception.
 */
public final class IdemponentRetryException extends RuntimeException {

    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = 480732299475266442L;


    /**
     * @param cause Cause
     */
    public IdemponentRetryException(Throwable cause) {
        super(cause);
    }

}
