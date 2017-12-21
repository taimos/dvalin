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


import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.taimos.daemon.log4j.Log4jLoggingConfigurer;
import de.taimos.daemon.spring.SpringDaemonTestRunner;
import de.taimos.daemon.spring.SpringDaemonTestRunner.RunnerConfiguration;
import de.taimos.dvalin.interconnect.core.spring.test.ADaemonTest;
import de.taimos.dvalin.interconnect.demo.model.UserIVO_v1;
import de.taimos.dvalin.interconnect.demo.model.requests.CreateUserIVO_v1;
import de.taimos.dvalin.interconnect.demo.model.requests.FindUserIVO_v1;
import de.taimos.dvalin.interconnect.model.ivo.util.IVOQueryResultIVO_v1;
import de.taimos.dvalin.interconnect.model.service.DaemonError;


@RunWith(SpringDaemonTestRunner.class)
@RunnerConfiguration(svc = "user-service", loggingConfigurer = Log4jLoggingConfigurer.class, config = TestConfig.class)
public class HandlerTest extends ADaemonTest<Handler> {

    @Test
    public void testUserlist() throws DaemonError {
        IVOQueryResultIVO_v1<UserIVO_v1> users = this.handler().findUsers(new FindUserIVO_v1.FindUserIVO_v1Builder().build());
        Assert.assertEquals(0, users.getElements().size());
    }

    @Test
    public void testCreateUser() throws DaemonError {
        String name = UUID.randomUUID().toString();
        String city = UUID.randomUUID().toString();

        UserIVO_v1.UserIVO_v1Builder uB = new UserIVO_v1.UserIVO_v1Builder();
        uB.withName(name);
        uB.withCity(city);

        CreateUserIVO_v1.CreateUserIVO_v1Builder b = new CreateUserIVO_v1.CreateUserIVO_v1Builder();
        b.withValue(uB.build());
        UserIVO_v1 user = this.handler().createUser(b.build());

        Assert.assertEquals(name, user.getName());
        Assert.assertEquals(city, user.getCity());
    }

}
