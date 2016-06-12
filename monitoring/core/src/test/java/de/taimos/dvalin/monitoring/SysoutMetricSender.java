package de.taimos.dvalin.monitoring;

import de.taimos.daemon.spring.annotations.TestComponent;

@TestComponent
public class SysoutMetricSender implements MetricSender {

    @Override
    public void sendMetric(MetricInfo metric, Double value) {
        String s = String.format("Sending metric %s/%s with value %s %s %s", metric.getNamespace(), metric.getMetric(), value, metric.getUnit(), metric.getDimensions().toString());
        System.out.println(s);
    }
}
