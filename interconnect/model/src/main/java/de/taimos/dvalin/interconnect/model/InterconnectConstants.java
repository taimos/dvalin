package de.taimos.dvalin.interconnect.model;

/**
 * Interconnect constants.
 */
public interface InterconnectConstants {

	/** maximum time to wait when sending a message over the message queue (in milliseconds) */
	public static final long IVO_SENDTIMEOUT = 10000;

	/** maximum time to wait when receiving a message over the message queue (in milliseconds) */
	public static final long IVO_RECEIVETIMEOUT = 10000;

	/** the message priority to use when sending a message over the message queue */
	public static final int IVO_MSGPRIORITY = 5;

	/** name of the boolean message property that indicates whether the message contains a regular result or an error object */
	public static final String MSGPROP_ERROR = "error";

    /** Constant for the system property that holds the AES key for message encryption */
    public static final String PROPERTY_CRYPTO_AESKEY = "interconnect.crypto.aes";

    /** Constant for the system property that holds the Signature key for message encryption */
    public static final String PROPERTY_CRYPTO_SIGNATURE = "interconnect.crypto.signature";

}
