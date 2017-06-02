package de.taimos.dvalin.jaxrs.endpoints.zendesk;

/*-
 * #%L
 * JAX-RS support for dvalin using Apache CXF
 * %%
 * Copyright (C) 2015 - 2017 Taimos GmbH
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Ticket {
	
	private String subject;
	private String comment;
	private String requesterName;
	private String requesterEMail;
	private Map<Integer, String> customFields = new HashMap<>();
	
	
	public String getSubject() {
		return this.subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String getComment() {
		return this.comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getRequesterName() {
		return this.requesterName;
	}
	
	public void setRequesterName(String requesterName) {
		this.requesterName = requesterName;
	}
	
	public String getRequesterEMail() {
		return this.requesterEMail;
	}
	
	public void setRequesterEMail(String requesterEMail) {
		this.requesterEMail = requesterEMail;
	}
	
	public Map<Integer, String> getCustomFields() {
		return this.customFields;
	}
	
	public void setCustomFields(Map<Integer, String> customFields) {
		this.customFields = customFields;
	}
	
	public Map<String, Object> toJsonMap() {
		Map<String, Object> map = new HashMap<>();
		
		Map<String, Object> ticket = new HashMap<>();
		ticket.put("subject", this.subject);
		
		Map<String, Object> commentMap = new HashMap<>();
		commentMap.put("body", this.comment);
		ticket.put("comment", commentMap);
		
		Map<String, Object> requester = new HashMap<>();
		requester.put("name", this.requesterName);
		requester.put("email", this.requesterEMail);
		ticket.put("requester", requester);
		
		List<Map<String, Object>> custom = new ArrayList<>();
		for (Entry<Integer, String> field : this.customFields.entrySet()) {
			Map<String, Object> fieldMap = new HashMap<>();
			fieldMap.put("id", field.getKey());
			fieldMap.put("value", field.getValue());
			custom.add(fieldMap);
		}
		ticket.put("custom_fields", custom);
		
		map.put("ticket", ticket);
		return map;
	}
	
}
