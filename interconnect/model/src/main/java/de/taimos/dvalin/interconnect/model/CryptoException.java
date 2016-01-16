package de.taimos.dvalin.interconnect.model;

public class CryptoException extends Exception {

	private static final long serialVersionUID = 1L;


	/**
	 * @param message the exception message
	 * @param cause the root cause
	 */
	public CryptoException(String message, Throwable cause) {
		super(message, cause);
	}

}
