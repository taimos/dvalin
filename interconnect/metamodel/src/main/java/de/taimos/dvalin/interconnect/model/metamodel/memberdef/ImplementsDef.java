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
import jakarta.xml.bind.annotation.XmlType;

/**
 * Defines an interface to be implemented by a clazzdef
 */
@XmlType
public class ImplementsDef implements INamedMemberDef {

    private String name;
    private String pkgName;
    private Boolean skipOnFilter = false;

    /**
     * default constructor
     */
    public ImplementsDef() {
        //default constructor
    }

    /**
     * @param clazz the clazz to use
     */
    public ImplementsDef(Class<?> clazz) {
        this.setName(clazz.getSimpleName());
        this.setPkgName(clazz.getPackage().getName());
    }

    /**
     * @param name    the name
     * @param pkgName the package name
     */
    public ImplementsDef(String name, String pkgName) {
        this.name = name;
        this.pkgName = pkgName;
    }

    /**
     * @return the name
     */
    @XmlAttribute(required = true)
    public String getName() {
        return this.name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the pkgName
     */
    @XmlAttribute(required = true)
    public String getPkgName() {
        return this.pkgName;
    }

    /**
     * @param pkgName the pkgName to set
     */
    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    /**
     * @return the skipOnFilter
     */
    @XmlAttribute(required = false)
    public Boolean getSkipOnFilter() {
        return this.skipOnFilter;
    }

    /**
     * @param skipOnFilter the skipOnFilter to set
     */
    public void setSkipOnFilter(Boolean skipOnFilter) {
        this.skipOnFilter = skipOnFilter;
    }
}
