package de.taimos.dvalin.interconnect.demo;

/*
 * #%L
 * Dvalin interconnect demo project
 * %%
 * Copyright (C) 2016 Taimos GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.taimos.dvalin.interconnect.core.daemon.Interconnect;
import de.taimos.dvalin.interconnect.core.spring.RequestHandler;
import de.taimos.dvalin.interconnect.demo.api.IRegistrationService;
import de.taimos.dvalin.interconnect.demo.api.IUserService;
import de.taimos.dvalin.interconnect.demo.api.UserError;
import de.taimos.dvalin.interconnect.demo.model.RegUserIVO_v1;
import de.taimos.dvalin.interconnect.demo.model.UserIVO_v1;
import de.taimos.dvalin.interconnect.demo.model.requests.CreateUserIVO_v1;
import de.taimos.dvalin.interconnect.demo.model.requests.DeleteUserIVO_v1;
import de.taimos.dvalin.interconnect.demo.model.requests.FindUserByIdIVO_v1;
import de.taimos.dvalin.interconnect.demo.model.requests.FindUserIVO_v1;
import de.taimos.dvalin.interconnect.demo.model.requests.UpdateUserIVO_v1;
import de.taimos.dvalin.interconnect.model.ivo.util.IVOQueryResultIVO_v1;
import de.taimos.dvalin.interconnect.model.service.ADaemonHandler;
import de.taimos.dvalin.interconnect.model.service.DaemonError;

@RequestHandler
public class Handler extends ADaemonHandler implements IUserService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private static final ConcurrentHashMap<Long, UserIVO_v1> users = new ConcurrentHashMap<>();

    @Interconnect
    private IRegistrationService regService;

    @Override
    public UserIVO_v1 createUser(CreateUserIVO_v1 ivo) throws DaemonError {
        long max = 0;
        for (Long id : users.keySet()) {
            if (id > max) {
                max = id;
            }
        }
        UserIVO_v1 newUser = ivo.getValue();
        UserIVO_v1.UserIVO_v1Builder builder = newUser.createBuilder();
        builder.withId(Long.toString(max + 1));
        UserIVO_v1 createdUser = builder.build();

        RegUserIVO_v1.RegUserIVO_v1Builder regB = new RegUserIVO_v1.RegUserIVO_v1Builder();
        regB.withName(createdUser.getName());
        regB.withUserId(createdUser.getIdAsLong());
        this.regService.registerUser(regB.build());

        users.put(createdUser.getIdAsLong(), createdUser);
        return createdUser;
    }

    @Override
    public UserIVO_v1 saveUser(UpdateUserIVO_v1 ivo) throws DaemonError {
        UserIVO_v1 user = ivo.getValue();
        if (!users.containsKey(user.getIdAsLong())) {
            throw new DaemonError(UserError.USER_NOT_FOUND);
        }
        users.put(user.getIdAsLong(), user);
        return user;
    }

    @Override
    public UserIVO_v1 findById(FindUserByIdIVO_v1 ivo) throws DaemonError {
        if (!users.containsKey(Long.valueOf(ivo.getIds().get(0)))) {
            throw new DaemonError(UserError.USER_NOT_FOUND);
        }
        return users.get(Long.valueOf(ivo.getIds().get(0)));
    }

    @Override
    public void deleteUser(DeleteUserIVO_v1 ivo) throws DaemonError {
        if (!users.containsKey(Long.valueOf(ivo.getIds().get(0)))) {
            throw new DaemonError(UserError.USER_NOT_FOUND);
        }
        users.remove(Long.valueOf(ivo.getIds().get(0)));
    }

    @Override
    public IVOQueryResultIVO_v1<UserIVO_v1> findUsers(FindUserIVO_v1 query) throws DaemonError {
        this.LOGGER.info("Fetching users");
        // TODO filter
        ArrayList<UserIVO_v1> usersList = new ArrayList<>(users.values());
        return IVOQueryResultIVO_v1.create(usersList, usersList.size());
    }
}
