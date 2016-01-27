package de.taimos.dvalin.interconnect.model.metamodel;

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
