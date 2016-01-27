package de.taimos.dvalin.interconnect.model.metamodel;

/**
 * the supported content types
 */
public enum ContentType {
    /**
     * boolean
     */
    Boolean,
    /**
     * date
     */
    Date,
    /**
     * decimal
     */
    Decimal,
    /**
     * integer
     */
    Integer,
    /**
     * long
     */
    Long,
    /**
     * string
     */
    String,
    /**
     * IVOs (requires ivoName, package name and version)
     */
    IVO,
    /**
     * enums (requires clazz and the pacakge attribute)
     */
    Enum,
    /**
     * UUID
     */
    UUID,
    /**
     * interconnect objects (requires clazz and the package attribute)
     */
    InterconnectObject
}
