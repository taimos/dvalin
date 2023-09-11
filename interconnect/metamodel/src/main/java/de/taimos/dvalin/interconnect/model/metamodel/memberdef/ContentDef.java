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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Content definition for tables, maps and collections
 */
@XmlType
@XmlAccessorType(XmlAccessType.PROPERTY)
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

    /**
     * @param isInterface true if interface should be used
     * @return the type string
     */
    public String getVersionedType(boolean isInterface) {
        return (isInterface ? "I" : "") + this.getIvoName() + "IVO_v" + this.getVersion();
    }

    /**
     * @param asInterface true if interface should be used
     * @return the type string
     */
    public String getClazzName(boolean asInterface) {
        return (asInterface ? "I" : "") + this.getIvoName() + "IVO_v" + this.getVersion();
    }

    /**
     * @param asInterface is an interface
     * @return the path of the content def
     */
    public String getPath(boolean asInterface) {
        return this.getPkgName() + "." + this.getClazzName(asInterface);
    }

    /**
     * @param isInterface type as string for interface use
     * @return the type as string
     */
    public String getTypeAsString(boolean isInterface) {
        String result;
        switch(this.getType()) {
            case IVO:
                result = "? extends " + (this.getIvoName() == null ? "IVO" : (isInterface ? "I" : "") + this.getIvoName() + "IVO_v" + this.getVersion());
                break;
            case InterconnectObject:
            case Enum:
                result = this.getClazz();
                break;
            default:
                result = this.getType().getType();
                break;
        }
        return result;
    }
}
