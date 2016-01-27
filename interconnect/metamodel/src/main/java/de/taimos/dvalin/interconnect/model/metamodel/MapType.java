package de.taimos.dvalin.interconnect.model.metamodel;

import javax.xml.bind.annotation.XmlType;

/**
 * the supported map types
 */
@XmlType
public enum MapType {
	/**
	 * a normal map
	 */
	Map,
	/**
	 * a guava multimap
	 */
	Multimap
}
