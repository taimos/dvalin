/**
 *
 */
package de.taimos.dvalin.jaxrs.monitoring;

/*
 * #%L
 * Daemon with Spring and CXF
 * %%
 * Copyright (C) 2013 - 2015 Taimos GmbH
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

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.cxf.Bus;
import org.apache.cxf.annotations.Provider;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;

import de.taimos.dvalin.jaxrs.JaxRsComponent;

@JaxRsComponent
@Provider(value = Provider.Type.Feature)
public class MonitoringFeature extends AbstractFeature {

    private static final String MDC_METHOD = "calledMethod";
    private static final String MDC_CLASS = "calledClass";
    private static final String MDC_REQUEST = "requestId";
    private static final String MDC_URI = "requestURI";

    private static final String CXF_METHOD = "org.apache.cxf.resource.method";
    private static final String CXF_URI = "org.apache.cxf.request.uri";

    private static final String CXF_HEADERMAP = "org.apache.cxf.message.Message.PROTOCOL_HEADERS";
    private static final String REQUEST_HEADER = "X-Request-ID";

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringFeature.class);

    @Value("${jaxrs.slowlog:1000}")
    private Integer durationThreshold;

    @Override
    protected void initializeProvider(InterceptorProvider provider, Bus bus) {
        provider.getInInterceptors().add(new AbstractPhaseInterceptor<Message>(Phase.RECEIVE) {

            @Override
            public void handleMessage(Message message) {
                MonitoringFeature.this.startMessage(message);
            }
        });
        provider.getInInterceptors().add(new AbstractPhaseInterceptor<Message>(Phase.PRE_INVOKE) {

            @Override
            public void handleMessage(Message message) {
                MonitoringFeature.this.invokeMessage(message);
            }
        });
        provider.getOutInterceptors().add(new AbstractPhaseInterceptor<Message>(Phase.PRE_PROTOCOL) {

            @Override
            public void handleMessage(Message message) {
                MonitoringFeature.this.endMessage(message);
            }
        });
    }

    @SuppressWarnings("unchecked")
    void startMessage(Message m) {
        final String messageId;
        Map<String, List<String>> headerMap = (Map<String, List<String>>) m.get(MonitoringFeature.CXF_HEADERMAP);
        if (headerMap != null && headerMap.containsKey(MonitoringFeature.REQUEST_HEADER) && !headerMap.get(MonitoringFeature.REQUEST_HEADER).isEmpty()) {
            messageId = headerMap.get(MonitoringFeature.REQUEST_HEADER).get(0);
        } else {
            messageId = UUID.randomUUID().toString();
        }
        String requestURI = (String) m.get(MonitoringFeature.CXF_URI);
        final InvocationInstance i = new InvocationInstance(messageId, requestURI);
        i.start();
        m.setContent(InvocationInstance.class, i);
        m.getExchange().put(InvocationInstance.class, i);

        MDC.put(MonitoringFeature.MDC_REQUEST, i.getMessageId());
        MDC.put(MonitoringFeature.MDC_URI, i.getRequestURI());
    }

    void endMessage(Message m) {
        InvocationInstance i = this.stopInstance(m);
        if (i.getDuration() > this.durationThreshold) {
            MonitoringFeature.LOGGER.warn("SLOW RESPONSE: " + i.toString());
        }
    }

    private InvocationInstance stopInstance(Message m) {
        InvocationInstance i = m.getExchange().get(InvocationInstance.class);
        i.stop();
        MonitoringFeature.LOGGER.debug(i.toString());
        // Clear MDC
        MDC.remove(MonitoringFeature.MDC_REQUEST);
        MDC.remove(MonitoringFeature.MDC_URI);
        MDC.remove(MonitoringFeature.MDC_CLASS);
        MDC.remove(MonitoringFeature.MDC_METHOD);
        return i;
    }

    void invokeMessage(Message message) {
        InvocationInstance i = message.getExchange().get(InvocationInstance.class);
        final Method method = (Method) message.get(MonitoringFeature.CXF_METHOD);
        if ((method != null) && (i != null)) {
            i.setCalledMethod(method);
            MDC.put(MonitoringFeature.MDC_CLASS, i.getCalledClass());
            MDC.put(MonitoringFeature.MDC_METHOD, i.getCalledMethodName());
        }
    }
}
