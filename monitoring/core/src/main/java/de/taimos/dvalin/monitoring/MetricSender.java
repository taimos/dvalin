package de.taimos.dvalin.monitoring;

/**
 * Interface to send metric data to the monitoring system
 */
public interface MetricSender {

    /**
     * send the given value to the given metric
     *
     * @param metric the target metric
     * @param value  the value to send
     */
    void sendMetric(MetricInfo metric, Double value);

}
