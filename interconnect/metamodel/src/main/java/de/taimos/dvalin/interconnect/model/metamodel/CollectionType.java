package de.taimos.dvalin.interconnect.model.metamodel;

import javax.xml.bind.annotation.XmlType;

/**
 * the supported collection types
 *
 */
@XmlType
public enum CollectionType {
	/**
	 * a set
	 */
	Set,
	/**
	 * a list
	 */
	List
}
