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
 * IVO member definition
 */
@XmlType
public class IVOMemberDef extends MemberDef {

    private String ivoName;
    private Integer version;
    private String pkgName;


    /**
     * @return the ivoName
     */
    @XmlAttribute(required = true)
    public String getIvoName() {
        return this.ivoName;
    }

    /**
     * @param ivoName the ivoName to set
     */
    public void setIvoName(String ivoName) {
        this.ivoName = ivoName;
    }

    /**
     * @return the version
     */
    @XmlAttribute(required = true)
    public Integer getVersion() {
        return this.version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     * @return the package name
     */
    @XmlAttribute(required = false)
    public String getPkgName() {
        return this.pkgName;
    }

    /**
     * @param pkgName the package name
     */
    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    /**
     * @param asInterface true if interface should be used
     * @return the type string
     */
    public String getIVOClazzName(boolean asInterface) {
        return (asInterface ? "I" : "") + this.getIvoName() + "IVO_v" + this.getVersion();
    }

    public String getIVOPath(boolean asInterface) {
        return this.getPkgName() + "." + this.getIVOClazzName(asInterface);
    }

    @Override
    public String getTypeAsString(boolean isInterface) {
        return this.getIvoName() == null ? "IVO" : this.getIVOClazzName(isInterface);
    }
}
