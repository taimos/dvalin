package de.taimos.dvalin.monitoring.aws;

/*-
 * #%L
 * Dvalin monitoring service implementation for AWS CloudWatch
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

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import de.taimos.dvalin.monitoring.MetricInfo;
import de.taimos.dvalin.monitoring.MetricUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;

@ExtendWith(MockitoExtension.class)
class AWSMetricSenderTest {

    @Mock
    private AmazonCloudWatch cloudWatch;

    private AWSMetricSender sender;

    @BeforeEach
    void setUp() throws Exception {
        this.sender = new AWSMetricSender();

        Field field = AWSMetricSender.class.getDeclaredField("cloudWatch");
        field.setAccessible(true);
        field.set(this.sender, this.cloudWatch);
    }

    @Test
    void sendMetric() {
        final String ns = "My/Namespace";
        final String metric = "Test metric";
        final MetricUnit unit = MetricUnit.Count;
        MetricInfo info = new MetricInfo(ns, metric, unit);

        final int val = new Random().nextInt(100);

        Mockito.doAnswer((Answer<Object>) invocationOnMock -> {
            PutMetricDataRequest req = (PutMetricDataRequest) invocationOnMock.getArguments()[0];
            Assertions.assertEquals(ns, req.getNamespace());
            List<MetricDatum> data = req.getMetricData();
            Assertions.assertEquals(1, data.size());
            MetricDatum datum = data.get(0);
            Assertions.assertEquals(metric, datum.getMetricName());
            Assertions.assertEquals(unit.toString(), datum.getUnit());
            Assertions.assertEquals(Double.valueOf(val), datum.getValue());
            return null;
        }).when(this.cloudWatch).putMetricData(Mockito.any(PutMetricDataRequest.class));

        this.sender.sendMetric(info, (double) val);
    }

    @Test
    void sendMetricWithDimension() {
        final String ns = "My/Namespace";
        final String metric = "Test metric";
        final MetricUnit unit = MetricUnit.Count;
        final String dimensionName = "id";
        final String dimensionValue = "someId";
        MetricInfo info = new MetricInfo(ns, metric, unit);
        info.withDimension(dimensionName, dimensionValue);

        final int val = new Random().nextInt(100);

        Mockito.doAnswer((Answer<Object>) invocationOnMock -> {
            PutMetricDataRequest req = (PutMetricDataRequest) invocationOnMock.getArguments()[0];
            Assertions.assertEquals(ns, req.getNamespace());
            List<MetricDatum> data = req.getMetricData();
            Assertions.assertEquals(1, data.size());
            MetricDatum datum = data.get(0);
            Assertions.assertEquals(metric, datum.getMetricName());
            Assertions.assertEquals(unit.toString(), datum.getUnit());
            Assertions.assertEquals(Double.valueOf(val), datum.getValue());

            Assertions.assertEquals(1, datum.getDimensions().size());
            Dimension dimension = datum.getDimensions().get(0);
            Assertions.assertEquals(dimensionName, dimension.getName());
            Assertions.assertEquals(dimensionValue, dimension.getValue());
            return null;
        }).when(this.cloudWatch).putMetricData(Mockito.any(PutMetricDataRequest.class));

        this.sender.sendMetric(info, (double) val);
    }

    @Test()
    void missingNamespace() {
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> new MetricInfo(null, "someMetric", MetricUnit.Count));
    }

    @Test()
    void missingMetric() {
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> new MetricInfo("Some/Namespace", null, MetricUnit.Count));
    }

    @Test()
    void missingUnit() {
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> new MetricInfo("Some/Namespace", "someMetric", null));
    }

    @Test()
    void emptyNamespace() {
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> new MetricInfo("", "someMetric", MetricUnit.Count));
    }

    @Test()
    void emptyMetric() {
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> new MetricInfo("Some/Namespace", "", MetricUnit.Count));
    }

    @Test()
    void missingDimensionName() {
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> new MetricInfo("Some/Namespace", "someMetric", MetricUnit.Count).withDimension(null, "value"));
    }

    @Test()
    void emptyDimensionName() {
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> new MetricInfo("Some/Namespace", "someMetric", MetricUnit.Count).withDimension("", "value"));
    }

    @Test()
    void missingDimensionValue() {
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> new MetricInfo("Some/Namespace", "someMetric", MetricUnit.Count).withDimension("name", null));
    }

    @Test()
    void emptyDimensionValue() {
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> new MetricInfo("Some/Namespace", "someMetric", MetricUnit.Count).withDimension("name", ""));
    }

}
