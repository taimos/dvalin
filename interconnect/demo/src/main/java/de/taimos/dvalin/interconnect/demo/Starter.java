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

import java.util.Map;

import de.taimos.daemon.DaemonStarter;
import de.taimos.daemon.log4j.Log4jLoggingConfigurer;
import de.taimos.daemon.spring.SpringDaemonAdapter;
import de.taimos.dvalin.interconnect.core.MessageConnector;

/**
 * Created by thoeger on 17.01.16.
 */
public class Starter extends SpringDaemonAdapter {

    public static void main(String[] args) {
        Log4jLoggingConfigurer.setup();
        DaemonStarter.startDaemon("user-service", new Starter());
    }

    @Override
    protected void loadBasicProperties(Map<String, String> map) {
        map.put(MessageConnector.SYSPROP_IBROKERURL, "failover:tcp://localhost:61616");
    }
}
