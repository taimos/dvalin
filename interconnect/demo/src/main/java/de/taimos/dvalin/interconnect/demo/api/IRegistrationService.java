package de.taimos.dvalin.interconnect.demo.api;

import de.taimos.dvalin.interconnect.demo.model.RegUserIVO_v1;
import de.taimos.dvalin.interconnect.model.service.Daemon;
import de.taimos.dvalin.interconnect.model.service.DaemonError;
import de.taimos.dvalin.interconnect.model.service.DaemonRequestMethod;
import de.taimos.dvalin.interconnect.model.service.IDaemon;

@Daemon(name = "registration-service")
public interface IRegistrationService extends IDaemon {

    @DaemonRequestMethod(idempotent = false)
    void registerUser(RegUserIVO_v1 ivo) throws DaemonError;

}
