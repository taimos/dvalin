package de.taimos.dvalin.monitoring.logging;

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
