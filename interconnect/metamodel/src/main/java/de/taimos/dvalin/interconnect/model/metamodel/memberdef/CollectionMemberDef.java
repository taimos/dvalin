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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.List;
import java.util.Set;

/**
 * Definition of a collection type clazz member
 */
@XmlType
public class CollectionMemberDef extends MemberDef implements IMultiMember {


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
    public String getTypeAsString(boolean isInterface) {
        StringBuilder builder = new StringBuilder();
        switch(this.getCollectionType()) {
            case List:
                builder.append(List.class.getSimpleName());
                break;
            case Set:
                builder.append(Set.class.getSimpleName());
                break;
        }
        builder.append("<");
        builder.append(this.getContentDef().getTypeAsString(isInterface));
        builder.append(">");
        return builder.toString();
    }
}
