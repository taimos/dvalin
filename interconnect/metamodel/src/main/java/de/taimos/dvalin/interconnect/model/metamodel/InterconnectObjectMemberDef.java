package de.taimos.dvalin.interconnect.model.metamodel;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Interconnect object member
 */
public class InterconnectObjectMemberDef extends MemberDef {

    private String clazz;
    private String pkgName;


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

}
