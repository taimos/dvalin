package de.taimos.daemon.log4j;

/*
 * #%L
 * Daemon Library Log4j extension
 * %%
 * Copyright (C) 2012 - 2016 Taimos GmbH
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

import de.taimos.daemon.DaemonStarter;

public class JSONLayout extends Layout {
	
	@Override
	public void activateOptions() {
		//
	}
	
	@Override
	public String format(LoggingEvent event) {
		return this.createJSON(event);
	}
	
	@Override
	public boolean ignoresThrowable() {
		return false;
	}
	
	@Override
	public String getContentType() {
		return "application/json";
	}
	
	private String createJSON(LoggingEvent event) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		
		Map<String, Object> log = new HashMap<>();
		log.put("daemon", DaemonStarter.getDaemonName());
		log.put("instance", DaemonStarter.getInstanceId());
		log.put("host", DaemonStarter.getHostname());
		log.put("phase", DaemonStarter.getCurrentPhase().name());
		log.put("timestamp", sdf.format(new Date(event.getTimeStamp())));
		log.put("level", event.getLevel().toString());
		log.put("source", event.getLoggerName());
		log.put("message", event.getRenderedMessage());
		log.put("thread", event.getThreadName());
		
		if (event.getThrowableInformation() != null) {
			Throwable throwable = event.getThrowableInformation().getThrowable();
			log.put("throwable", throwable.toString());
			log.put("stacktrace", this.getStacktrace(throwable));
		}
		if (event.getProperties() != null) {
			log.put("mdc", event.getProperties());
		}
		StringBuilder sb = new StringBuilder();
		this.addObject(sb, log);
		sb.append(Layout.LINE_SEP);
		return sb.toString();
	}
	
	private List<String> getStacktrace(Throwable throwable) {
		List<String> stacktrace = new ArrayList<>();
		for (StackTraceElement ste : throwable.getStackTrace()) {
			stacktrace.add(ste.toString());
		}
		if (throwable.getCause() != null) {
			stacktrace.add("Caused By: " + throwable.getCause().toString());
			stacktrace.addAll(this.getStacktrace(throwable.getCause()));
		}
		return stacktrace;
	}
	
	private void addObject(StringBuilder sb, Map<String, Object> map) {
		sb.append("{");
		Set<Entry<String, Object>> entrySet = map.entrySet();
		boolean first = true;
		for (Entry<String, Object> entry : entrySet) {
			if (first) {
				first = false;
			} else {
				this.addSeparator(sb);
			}
			this.addField(sb, entry.getKey(), entry.getValue());
		}
		sb.append("}");
	}
	
	private void addField(StringBuilder sb, String field, Object value) {
		// Add field name
		sb.append("\"");
		sb.append(this.cleanString(field));
		sb.append("\":");
		
		this.addValue(sb, value);
	}
	
	private void addSeparator(StringBuilder sb) {
		sb.append(",");
	}
	
	private void addArray(StringBuilder sb, Object... values) {
		sb.append("[");
		for (int i = 0; i < values.length; i++) {
			this.addValue(sb, values[i]);
			if (i != (values.length - 1)) {
				this.addSeparator(sb);
			}
		}
		sb.append("]");
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	private void addValue(StringBuilder sb, Object value) {
		if (value instanceof String) {
			sb.append("\"");
			sb.append(this.cleanString((String) value));
			sb.append("\"");
		} else if (value instanceof Map) {
			this.addObject(sb, (Map<String, Object>) value);
		} else if (value instanceof Object[]) {
			this.addArray(sb, (Object[]) value);
		} else if (value instanceof List) {
			this.addArray(sb, ((List) value).toArray());
		} else {
			throw new RuntimeException("Invalid value: " + value);
		}
	}
	
	private String cleanString(String value) {
		return value.replaceAll("\"", "");
	}
}
