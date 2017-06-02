package de.taimos.dvalin.monitoring;

/*-
 * #%L
 * Dvalin monitoring service
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
