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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Map member definition
 */
@XmlType
public class MapMemberDef extends MemberDef {


    private MapType mapType;
    private ContentDef keyContent;
    private ContentDef valueContent;


    /**
     * @return the mapType
     */
    @XmlAttribute(required = true)
    public MapType getMapType() {
        return this.mapType;
    }

    /**
     * @param mapType the mapType to set
     */
    public void setMapType(MapType mapType) {
        this.mapType = mapType;
    }

    /**
     * @return the keyContent
     */
    @XmlElement(name = "keyContent", type = ContentDef.class, required = true)
    public ContentDef getKeyContent() {
        return this.keyContent;
    }

    /**
     * @param keyContent the keyContent to set
     */
    public void setKeyContent(ContentDef keyContent) {
        this.keyContent = keyContent;
    }

    /**
     * @return the valueContent
     */
    @XmlElement(name = "valueContent", type = ContentDef.class, required = true)
    public ContentDef getValueContent() {
        return this.valueContent;
    }

    /**
     * @param valueContent the valueContent to set
     */
    public void setValueContent(ContentDef valueContent) {
        this.valueContent = valueContent;
    }

    @Override
    public Boolean isAFilterMember() {
        return false;
    }

}
