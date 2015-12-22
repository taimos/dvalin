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

public class InvocationInstance {
	
	private final UUID messageId;
	
	private long startNano;
	
	private long endNano;
	
	private long duration;
	
	private Method calledMethod;
	
	private String calledClass;
	
	private String calledMethodName;
	
	
	/**
	 * @param messageId the message id
	 */
	public InvocationInstance(UUID messageId) {
		this.messageId = messageId;
	}
	
	/**
	 * start timer
	 */
	public void start() {
		this.startNano = System.nanoTime();
	}
	
	/**
	 * stop timer
	 */
	public void stop() {
		this.endNano = System.nanoTime();
		this.duration = this.endNano - this.startNano;
		this.duration = this.duration / 1000000; // convert to ms
	}
	
	@Override
	public String toString() {
		final String msgString = "Message %s was %s ms inflight. Access was to class '%s' and method '%s'";
		return String.format(msgString, this.getMessageId(), this.getDuration(), this.getCalledClass(), this.getCalledMethod());
	}
	
	// #########################################
	// getter / setter
	// #########################################
	
	/**
	 * @return the startNano
	 */
	public long getStartNano() {
		return this.startNano;
	}
	
	/**
	 * @return the endNano
	 */
	public long getEndNano() {
		return this.endNano;
	}
	
	/**
	 * @return the calledMethod
	 */
	public Method getCalledMethod() {
		return this.calledMethod;
	}
	
	/**
	 * @param calledMethod the calledMethod to set
	 */
	public void setCalledMethod(Method calledMethod) {
		this.calledMethod = calledMethod;
		if ((calledMethod != null) && (calledMethod.getDeclaringClass() != null)) {
			this.calledMethodName = calledMethod.getName();
			this.calledClass = calledMethod.getDeclaringClass().getCanonicalName();
		}
	}
	
	/**
	 * @return the duration
	 */
	public long getDuration() {
		return this.duration;
	}
	
	/**
	 * @return the messageId
	 */
	public UUID getMessageId() {
		return this.messageId;
	}
	
	/**
	 * @return the calledClass
	 */
	public String getCalledClass() {
		return this.calledClass;
	}
	
	/**
	 * @return the calledMethodName
	 */
	public String getCalledMethodName() {
		return this.calledMethodName;
	}
	
}
