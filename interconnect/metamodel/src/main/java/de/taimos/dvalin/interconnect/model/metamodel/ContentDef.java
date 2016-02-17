package de.taimos.dvalin.interconnect.model.metamodel;

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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Content definition for tables, maps and collections
 */
@XmlType
public class ContentDef {

    private ContentType type;
    private String clazz;
    private String pkgName;
    private String ivoName;
    private Integer version;


    /**
     * @return the type
     */
    @XmlAttribute(required = true)
    public ContentType getType() {
        return this.type;
    }

    /**
     * @param type the type to set
     */
    public void setType(ContentType type) {
        this.type = type;
    }

    /**
     * @return the pkgName
     */
    @XmlAttribute(required = false)
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
     * @return the ivoName
     */
    @XmlAttribute(required = false)
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
    @XmlAttribute(required = false)
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
     * @return the clazz
     */
    @XmlAttribute(required = false)
    public String getClazz() {
        return this.clazz;
    }

    /**
     * @param clazz the clazz
     */
    public void setClazz(String clazz) {
        this.clazz = clazz;
    }
}
