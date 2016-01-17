package de.taimos.dvalin.interconnect.demo.api;

import de.taimos.dvalin.interconnect.demo.model.UserIVO_v1;
import de.taimos.dvalin.interconnect.demo.model.requests.CreateUserIVO_v1;
import de.taimos.dvalin.interconnect.demo.model.requests.DeleteUserIVO_v1;
import de.taimos.dvalin.interconnect.demo.model.requests.FindUserByIdIVO_v1;
import de.taimos.dvalin.interconnect.demo.model.requests.FindUserIVO_v1;
import de.taimos.dvalin.interconnect.demo.model.requests.UpdateUserIVO_v1;
import de.taimos.dvalin.interconnect.model.ivo.util.IVOQueryResultIVO_v1;
import de.taimos.dvalin.interconnect.model.service.Daemon;
import de.taimos.dvalin.interconnect.model.service.DaemonError;
import de.taimos.dvalin.interconnect.model.service.DaemonRequestMethod;
import de.taimos.dvalin.interconnect.model.service.IDaemon;

@Daemon(name = "user-service")
public interface IUserService extends IDaemon {

    @DaemonRequestMethod(idempotent = false)
    UserIVO_v1 createUser(CreateUserIVO_v1 ivo) throws DaemonError;

    @DaemonRequestMethod(idempotent = true)
    UserIVO_v1 saveUser(UpdateUserIVO_v1 ivo) throws DaemonError;

    @DaemonRequestMethod(idempotent = true)
    UserIVO_v1 findById(FindUserByIdIVO_v1 ivo) throws DaemonError;

    @DaemonRequestMethod(idempotent = true)
    void deleteUser(DeleteUserIVO_v1 ivo) throws DaemonError;

    @DaemonRequestMethod(idempotent = true)
    IVOQueryResultIVO_v1<UserIVO_v1> findUsers(FindUserIVO_v1 query) throws DaemonError;

}
