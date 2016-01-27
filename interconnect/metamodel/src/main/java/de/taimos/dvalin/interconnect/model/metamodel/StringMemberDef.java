package de.taimos.dvalin.interconnect.model.metamodel;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * string member of a class definition
 */
@XmlType
public class StringMemberDef extends MemberDef implements ILabelMember {

    private Boolean useAsLabel = false;


    /**
     * @return the useAsLabel
     */
    @XmlAttribute(required = false)
    public Boolean getUseAsLabel() {
        return this.useAsLabel;
    }

    /**
     * @param useAsLabel the useAsLabel to set
     */
    public void setUseAsLabel(Boolean useAsLabel) {
        this.useAsLabel = useAsLabel;
    }

    @Override
    public Boolean useAsLabel() {
        return this.getUseAsLabel();
    }

}
