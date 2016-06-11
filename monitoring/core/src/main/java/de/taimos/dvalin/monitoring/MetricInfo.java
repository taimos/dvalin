package de.taimos.dvalin.monitoring;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

public class MetricInfo {

    private final String namespace;
    private final String metric;
    private final Map<String, String> dimensions = new HashMap<>();
    private final MetricUnit unit;

    public MetricInfo(String namespace, String metric, MetricUnit unit) {
        Preconditions.checkArgument(namespace != null && !namespace.isEmpty());
        Preconditions.checkArgument(metric != null && !metric.isEmpty());
        Preconditions.checkArgument(unit != null);
        this.namespace = namespace;
        this.metric = metric;
        this.unit = unit;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public String getMetric() {
        return this.metric;
    }

    public Map<String, String> getDimensions() {
        return this.dimensions;
    }

    public MetricUnit getUnit() {
        return this.unit;
    }

    public MetricInfo withDimension(String name, String value) {
        Preconditions.checkArgument(name != null && !name.isEmpty());
        Preconditions.checkArgument(value != null && !value.isEmpty());
        this.dimensions.put(name, value);
        return this;
    }
}
