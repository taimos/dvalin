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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import de.taimos.dvalin.monitoring.MetricInfo;
import de.taimos.dvalin.monitoring.MetricSender;

/**
 * Send metric info to the console
 */
@Component
public class ConsoleMetricSender implements MetricSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleMetricSender.class);

    @Override
    public void sendMetric(MetricInfo metric, Double value) {
        LOGGER.info("Sending metric {}/{} with value {} {} {}", metric.getNamespace(), metric.getMetric(), value, metric.getUnit(), metric.getDimensions().toString());
    }
}
