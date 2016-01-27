package de.taimos.dvalin.interconnect.model.service;

import de.taimos.dvalin.interconnect.model.ivo.daemon.PingIVO;
import de.taimos.dvalin.interconnect.model.ivo.daemon.PongIVO;

/**
 * @see Daemon
 */
public interface IDaemon {

	// marker interface

	/**
	 * @param req {@link PingIVO}
	 * @return {@link PongIVO}
	 */
	@DaemonRequestMethod(idempotent = true)
	public PongIVO alive(PingIVO req);

}
