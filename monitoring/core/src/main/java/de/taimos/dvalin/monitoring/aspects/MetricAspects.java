package de.taimos.dvalin.monitoring.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import de.taimos.dvalin.monitoring.MetricInfo;
import de.taimos.dvalin.monitoring.MetricSender;
import de.taimos.dvalin.monitoring.MetricUnit;
import de.taimos.dvalin.monitoring.aspects.annotations.ExecutionTime;

@Aspect
@Component
public class MetricAspects {

    @Autowired
    private MetricSender metricSender;

    @Value("${serviceName:}")
    private String serviceName;

    @Around("@annotation(executionTime)")
    public Object meterExecutionTime(ProceedingJoinPoint pjp, ExecutionTime executionTime) throws Throwable {
        MetricInfo info = new MetricInfo(executionTime.namespace(), executionTime.metric(), MetricUnit.Milliseconds);
        if (executionTime.serviceNameDimension() && !StringUtils.isEmpty(serviceName)) {
            info.withDimension("service", serviceName);
        }

        long nanos = System.nanoTime();
        Object result = pjp.proceed();
        double time = (System.nanoTime() - nanos) / 1000d / 1000d;

        this.metricSender.sendMetric(info, time);
        return result;
    }
}
