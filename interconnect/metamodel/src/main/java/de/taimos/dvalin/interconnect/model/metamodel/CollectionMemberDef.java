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
 * Definition of a collection type clazz member
 */
@XmlType
public class CollectionMemberDef extends MemberDef {


    private CollectionType collectionType;
    private ContentDef contentDef;


    /**
     * @return the collectionType
     */
    @XmlAttribute(name = "collection", required = true)
    public CollectionType getCollectionType() {
        return this.collectionType;
    }

    /**
     * @param collectionType the collectionType to set
     */
    public void setCollectionType(CollectionType collectionType) {
        this.collectionType = collectionType;
    }

    /**
     * @return the contentDef
     */
    @XmlElement(name = "content", type = ContentDef.class, required = true)
    public ContentDef getContentDef() {
        return this.contentDef;
    }

    /**
     * @param contentDef the contentDef to set
     */
    public void setContentDef(ContentDef contentDef) {
        this.contentDef = contentDef;
    }

    @Override
    public Boolean isAFilterMember() {
        return false;
    }
}
