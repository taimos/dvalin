package de.taimos.dvalin.monitoring.logging;

/*-
 * #%L
 * Dvalin monitoring service implementation for logging framework
 * %%
 * Copyright (C) 2016 - 2017 Taimos GmbH
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

import de.taimos.daemon.log4j.Log4jLoggingConfigurer;
import de.taimos.dvalin.monitoring.MetricInfo;
import de.taimos.dvalin.monitoring.MetricUnit;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Random;

public class ConsoleMetricSenderTest {

    private ConsoleMetricSender sender;

    @Before
    public void setUp() throws Exception {
        new Log4jLoggingConfigurer().simpleLogging();

        this.sender = new ConsoleMetricSender();

        Field field = ConsoleMetricSender.class.getDeclaredField("LOGGER");
        field.setAccessible(true);
    }

    @Test
    public void sendMetric() {
        final String ns = "My/Namespace";
        final String metric = "Test metric";
        final MetricUnit unit = MetricUnit.Count;
        final String dimensionName = "id";
        final String dimensionValue = "someId";
        MetricInfo info = new MetricInfo(ns, metric, unit);
        info.withDimension(dimensionName, dimensionValue);

        final int val = new Random().nextInt(100);

        this.sender.sendMetric(info, (double) val);
    }
}
