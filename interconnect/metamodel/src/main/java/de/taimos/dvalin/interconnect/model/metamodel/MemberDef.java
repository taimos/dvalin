package de.taimos.dvalin.interconnect.model.metamodel;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * Abstract class defining a class member
 */
@XmlType
public abstract class MemberDef implements IFilterableMember {

    private String comment;
    private String name;
    private Boolean javaTransientFlag = false;
    private Boolean jsonTransientFlag = false;
    private Boolean orderTransient;
    private Boolean required = false;
    private Boolean filterable = false;
    private Boolean filterRequired = false;


    /**
     * @return the comment
     */
    @XmlAttribute(required = true)
    public String getComment() {
        return this.comment;
    }

    /**
     * @param comment the comment
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return the name
     */
    @XmlAttribute(required = true)
    public String getName() {
        return this.name;
    }

    /**
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the javaTransientFlag
     */
    @XmlAttribute(name = "javaTransient", required = false)
    public Boolean getJavaTransientFlag() {
        return this.javaTransientFlag;
    }

    /**
     * @param javaTransientFlag the javaTransientFlag to set
     */
    public void setJavaTransientFlag(Boolean javaTransientFlag) {
        this.javaTransientFlag = javaTransientFlag;
    }

    /**
     * @return the jsonTransientFlag
     */
    @XmlAttribute(name = "jsonTransient", required = false)
    public Boolean getJsonTransientFlag() {
        return this.jsonTransientFlag;
    }

    /**
     * @param jsonTransientFlag the jsonTransientFlag to set
     */
    public void setJsonTransientFlag(Boolean jsonTransientFlag) {
        this.jsonTransientFlag = jsonTransientFlag;
    }

    /**
     * @return the orderTransient
     */
    @XmlAttribute(required = false)
    public Boolean getOrderTransient() {
        return this.orderTransient;
    }

    /**
     * @param orderTransient the orderTransient to set
     */
    public void setOrderTransient(Boolean orderTransient) {
        this.orderTransient = orderTransient;
    }

    /**
     * @return true if required
     */
    @XmlAttribute(required = false)
    public Boolean getRequired() {
        return this.required;
    }

    /**
     * @param required true if required
     */
    public void setRequired(Boolean required) {
        this.required = required;
    }

    /**
     * @return the filterable
     */
    @XmlAttribute(required = false)
    public Boolean getFilterable() {
        return this.filterable;
    }

    /**
     * @param filterable the filterable to set
     */
    public void setFilterable(Boolean filterable) {
        this.filterable = filterable;
    }

    @Override
    @XmlTransient
    public Boolean isAFilterMember() {
        return this.filterable;
    }

    /**
     * @return the filterRequired
     */
    @XmlAttribute(required = false)
    public Boolean getFilterRequired() {
        return this.filterRequired;
    }

    /**
     * @param filterRequired the filterRequired to set
     */
    public void setFilterRequired(Boolean filterRequired) {
        this.filterRequired = filterRequired;
    }

}
