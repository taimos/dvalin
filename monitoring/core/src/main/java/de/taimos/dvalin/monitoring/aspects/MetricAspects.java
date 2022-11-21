package de.taimos.dvalin.monitoring.aspects;

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
        if (executionTime.serviceNameDimension() && StringUtils.hasText(this.serviceName)) {
            info.withDimension("service", this.serviceName);
        }

        long nanos = System.nanoTime();
        Object result = pjp.proceed();
        double time = (System.nanoTime() - nanos) / 1000d / 1000d;

        this.metricSender.sendMetric(info, time);
        return result;
    }
}
