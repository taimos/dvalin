/**
 * 
 */
package de.taimos.springcxfdaemon.monitoring;

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
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.lang.reflect.Method;
import java.util.UUID;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class MonitoringFeature extends AbstractFeature {
	
	private static final String MDC_METHOD = "calledMethod";
	private static final String MDC_CLASS = "calledClass";
	private static final String MDC_REQUEST = "requestId";
	
	private static final int DURATION_THRESHOLD = 10000;
	private static final String CXF_METHOD = "org.apache.cxf.resource.method";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringFeature.class);
	
	
	@Override
	public void initialize(Server server, Bus bus) {
		server.getEndpoint().getInInterceptors().add(new AbstractPhaseInterceptor<Message>(Phase.RECEIVE) {
			
			@Override
			public void handleMessage(Message message) {
				MonitoringFeature.this.startMessage(message);
			}
		});
		server.getEndpoint().getInInterceptors().add(new AbstractPhaseInterceptor<Message>(Phase.PRE_INVOKE) {
			
			@Override
			public void handleMessage(Message message) {
				MonitoringFeature.this.invokeMessage(message);
			}
		});
		server.getEndpoint().getOutInterceptors().add(new AbstractPhaseInterceptor<Message>(Phase.PRE_PROTOCOL) {
			
			@Override
			public void handleMessage(Message message) {
				MonitoringFeature.this.endMessage(message);
			}
		});
	}
	
	void startMessage(Message m) {
		final UUID messageId = UUID.randomUUID();
		final InvocationInstance i = new InvocationInstance(messageId);
		i.start();
		m.setContent(InvocationInstance.class, i);
		m.getExchange().put(InvocationInstance.class, i);
		
		MDC.put(MonitoringFeature.MDC_REQUEST, i.getMessageId().toString());
	}
	
	void endMessage(Message m) {
		InvocationInstance i = this.stopInstance(m);
		if (i.getDuration() > MonitoringFeature.DURATION_THRESHOLD) {
			MonitoringFeature.LOGGER.warn("SLOW RESPONSE: " + i.toString());
		}
	}
	
	private InvocationInstance stopInstance(Message m) {
		InvocationInstance i = m.getExchange().get(InvocationInstance.class);
		i.stop();
		MonitoringFeature.LOGGER.info(i.toString());
		// Clear MDC
		MDC.remove(MonitoringFeature.MDC_REQUEST);
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
