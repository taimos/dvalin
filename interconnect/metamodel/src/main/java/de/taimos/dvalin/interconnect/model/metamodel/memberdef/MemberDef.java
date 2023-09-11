package de.taimos.dvalin.interconnect.model.metamodel.memberdef;

/*
 * #%L
 * Dvalin interconnect metamodel for transfer data model
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

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Abstract class defining a class member
 *
 * @author psigloch
 */
@XmlType
public abstract class MemberDef implements IFilterableMember, INamedMemberDef {

    private String comment;
    private String name;
    private Boolean javaTransientFlag = false;
    private Boolean jsonTransientFlag = false;
    private Boolean orderTransient;
    private Boolean required = false;
    private Boolean filterOnly = false;
    private FilterableType filterable = FilterableType.none;

    /**
     * @param isInterface should type be returned as interface
     * @return the type as a string
     */
    public abstract String getTypeAsString(boolean isInterface);

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
     * @return the filterOnly
     */
    @XmlAttribute(required = false)
    public Boolean getFilterOnly() {
        return this.filterOnly;
    }

    /**
     * @param filterOnly the filterOnly to set
     */
    public void setFilterOnly(Boolean filterOnly) {
        this.filterOnly = filterOnly;
    }

    /**
     * @return the filterable
     */
    @XmlAttribute(required = false)
    public FilterableType getFilterable() {
        return this.filterable;
    }

    /**
     * @param filterable the filterable to set
     */
    public void setFilterable(FilterableType filterable) {
        this.filterable = filterable;
    }

    @Override
    @XmlTransient
    public Boolean isAFilterMember() {
        return this.filterable != FilterableType.none;
    }

}
