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
 * Enum clazz member definition
 */
@XmlType
public class EnumMemberDef extends MemberDef {

    private String clazz;
    private String pkgName;


    /**
     * @return the clazz
     */
    @XmlAttribute(required = true)
    public String getClazz() {
        return this.clazz;
    }

    /**
     * @param clazz the clazz to set
     */
    public void setClazz(String clazz) {
        this.clazz = clazz;
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

    @Override
    public String getTypeAsString(boolean isInterface) {
        return this.getClazz();
    }
}
