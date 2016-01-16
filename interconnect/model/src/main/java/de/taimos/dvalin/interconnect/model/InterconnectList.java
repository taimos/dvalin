package de.taimos.dvalin.interconnect.model;

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
