package de.taimos.dvalin.interconnect.model.metamodel;

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
