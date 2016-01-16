package de.taimos.dvalin.interconnect.model.service;

/**
 * The number of the DaemonError can only be interpreted if you know the sender of the DaemonError.<br>
 * Every sender must have an enum where the error numbers are specified.<br>
 */
public class DaemonError extends Exception {

	private static final long serialVersionUID = 1L;

	/** Number (never null). */
	private final DaemonErrorNumber number;


	/**
	 * @param aNumber Number
	 */
	public DaemonError(final DaemonErrorNumber aNumber) {
		super(((aNumber != null) ? aNumber.daemon() + " #" + aNumber.get() : ""));
		if (aNumber == null) {
			throw new IllegalArgumentException("number was null");
		}
		this.number = aNumber;
	}

	/**
	 * @param aNumber Number
	 * @param message Message
	 */
	public DaemonError(final DaemonErrorNumber aNumber, final String message) {
		super(message);
		if (aNumber == null) {
			throw new IllegalArgumentException("number was null");
		}
		this.number = aNumber;
	}

	/**
	 * @param aNumber Number
	 * @param cause Cause
	 */
	public DaemonError(final DaemonErrorNumber aNumber, final Throwable cause) {
		super((aNumber != null) ? aNumber.daemon() + " #" + aNumber.get() : "", cause);
		if (aNumber == null) {
			throw new IllegalArgumentException("number was null");
		}
		this.number = aNumber;
	}

	/**
	 * @param aNumber Number
	 * @param message Message
	 * @param cause Cause
	 */
	public DaemonError(final DaemonErrorNumber aNumber, final String message, final Throwable cause) {
		super(message, cause);
		if (aNumber == null) {
			throw new IllegalArgumentException("number was null");
		}
		this.number = aNumber;
	}

	/**
	 * @return Number
	 */
	public DaemonErrorNumber getNumber() {
		return this.number;
	}

}
