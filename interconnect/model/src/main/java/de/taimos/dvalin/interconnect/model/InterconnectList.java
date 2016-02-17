package de.taimos.dvalin.interconnect.model;

/*
 * #%L
 * Dvalin interconnect transfer data model
 * %%
 * Copyright (C) 2016 Taimos GmbH
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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.collect.Lists;


/**
 * @param <E> the content type
 */
public class InterconnectList<E extends InterconnectObject> implements InterconnectObject {

	private static final long serialVersionUID = -5710359147512408270L;

	@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
	private List<E> elements;


	/**
	 *
	 */
	public InterconnectList() {
		//
	}

	/**
	 * @param elements the elements
	 */
	public InterconnectList(List<E> elements) {
		super();
		this.elements = elements;
	}

	/**
	 * @return the elements
	 */
	public List<E> getElements() {
		return this.elements;
	}

	/**
	 * @param elements the elements to set
	 */
	public void setElements(List<E> elements) {
		this.elements = elements;
	}

    @Override
    public Object clone() {
        InterconnectList<E> list = new InterconnectList<>();
        list.setElements(Lists.newArrayList(this.elements));
        return list;
    }
}
