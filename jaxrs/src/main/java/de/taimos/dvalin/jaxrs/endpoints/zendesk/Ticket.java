package de.taimos.dvalin.jaxrs.endpoints.zendesk;

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
