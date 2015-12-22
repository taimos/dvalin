/**
 * 
 */
package de.taimos.dao.mongo;

import java.util.ArrayList;
import java.util.List;

import de.taimos.dao.AEntity;
import de.taimos.dao.mongo.links.DocumentLink;

/**
 * Copyright 2015 Taimos GmbH<br>
 * <br>
 *
 * @author thoeger
 * 		
 */
public class LinkObject extends AEntity {
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	
	private List<DocumentLink<LinkedObject>> links = new ArrayList<>();
	
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<DocumentLink<LinkedObject>> getLinks() {
		return this.links;
	}
	
	public void setLinks(List<DocumentLink<LinkedObject>> links) {
		this.links = links;
	}
	
	@Override
	public String toString() {
		return "LinkObject [name=" + this.name + ", links=" + this.links + "]";
	}
	
}
