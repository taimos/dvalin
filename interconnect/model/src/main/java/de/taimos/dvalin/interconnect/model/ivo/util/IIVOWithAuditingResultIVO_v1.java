package de.taimos.dvalin.interconnect.model.ivo.util;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * an ivo with its auditing
 *
 * @param <E> type of value object
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public interface IIVOWithAuditingResultIVO_v1<E extends IIVOAuditing> {

    /**
     * property constant for auditingElements property comment: the auditing elements (or null if not applicable)
     */
    public static final String PROP_AUDITINGELEMENTS = "auditingElements";
    /**
     * property constant for element property comment: the element retrieved by the request
     */
    public static final String PROP_ELEMENT = "element";


    /**
     * the possible results (or null if not applicable)
     *
     * @return the value for possibleResults
     **/
    public E getElement();

    /**
     * the elements retrieved by the request
     *
     * @return the value for elements
     **/
    public List<E> getAuditingElements();

    /**
     * @return a clone
     */
    public IIVOWithAuditingResultIVO_v1<E> clone();

}
