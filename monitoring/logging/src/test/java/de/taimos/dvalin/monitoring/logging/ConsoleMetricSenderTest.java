package de.taimos.dvalin.monitoring.logging;

import java.lang.reflect.Field;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import de.taimos.daemon.log4j.Log4jLoggingConfigurer;
import de.taimos.dvalin.monitoring.MetricInfo;
import de.taimos.dvalin.monitoring.MetricUnit;

public class ConsoleMetricSenderTest {

    private ConsoleMetricSender sender;
    private Logger targetLogger;

    @Before
    public void setUp() throws Exception {
        new Log4jLoggingConfigurer().simpleLogging();

        this.sender = new ConsoleMetricSender();

        Field field = ConsoleMetricSender.class.getDeclaredField("LOGGER");
        field.setAccessible(true);
        this.targetLogger = (Logger) field.get(null);
    }

    @Test
    public void sendMetric() throws Exception {
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
