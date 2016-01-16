package de.taimos.dvalin.interconnect.model;


import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Interface that must be implemented by all objects that are transported via the interconnect
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public interface InterconnectObject extends Serializable, Cloneable {

	// just a marker interface, yet an important one

    /**
     * @return a clone
     */
    Object clone();

}
