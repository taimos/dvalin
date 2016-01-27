package de.taimos.dvalin.interconnect.model.service;

import java.io.Serializable;

/**
 * @see DaemonError
 */
public interface DaemonErrorNumber extends Serializable {

	/**
	 * @return Number of the error (unique within a daemon, negative numbers are reserved for the framework!)
	 */
	int get();

	/**
	 * @return Daemon
	 */
	String daemon();

}
