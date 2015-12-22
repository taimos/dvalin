/**
 * 
 */
package de.taimos.dao.mongo;

import de.taimos.dao.mongo.links.AReferenceableEntity;

/**
 * Copyright 2015 Taimos GmbH<br>
 * <br>
 *
 * @author thoeger
 * 		
 */
public class LinkedObject extends AReferenceableEntity<LinkedObject> {
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	protected String getLabel() {
		return this.name;
	}
	
	@Override
	public String toString() {
		return "LinkedObject [name=" + this.name + "]";
	}
	
}
