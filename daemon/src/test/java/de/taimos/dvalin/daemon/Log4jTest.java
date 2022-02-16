package de.taimos.dvalin.daemon;

/*
 * #%L
 * Daemon Library Log4j extension
 * %%
 * Copyright (C) 2012 - 2016 Taimos GmbH
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

import de.taimos.daemon.DaemonLifecycleAdapter;
import de.taimos.daemon.DaemonProperties;
import de.taimos.daemon.DaemonStarter;
import de.taimos.daemon.log4j.Log4jDaemonProperties;
import de.taimos.daemon.log4j.Log4jLoggingConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.UUID;

public class Log4jTest extends DaemonLifecycleAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(Log4jTest.class);


	public static void main(String[] args) {
		System.setProperty(DaemonProperties.STARTUP_MODE, DaemonProperties.STARTUP_MODE_RUN);
//		System.setProperty(Log4jDaemonProperties.LOGGER_LAYOUT, Log4jDaemonProperties.LOGGER_LAYOUT_JSON);
		Log4jLoggingConfigurer.setup();
        DaemonStarter.getDaemonProperties().put(Log4jDaemonProperties.LOGGER_FILE, "true");
//      DaemonStarter.getDaemonProperties().put(Log4jDaemonProperties.ADDITIONAL_LOGGING, "de.taimos.dvalin.daemon.Log4jTest=DEBUG;");

        DaemonStarter.startDaemon("foobar", new Log4jTest());

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		DaemonStarter.stopService();
	}

	@Override
	public void started() {
		super.started();

        LOGGER.debug("Debug");
		Log4jTest.LOGGER.info("Message");
		Log4jTest.LOGGER.info("Message with \"quote\" in text");
		Log4jTest.LOGGER.warn("Warning", new RuntimeException("Failed", new IllegalArgumentException("Causing exception")));
        LOGGER.error("error");

		MDC.put("requestID", UUID.randomUUID().toString());
		Log4jTest.LOGGER.info("Request with MDC");
		MDC.remove("requestID");

        LOGGER.info("After Request");
	}

}
