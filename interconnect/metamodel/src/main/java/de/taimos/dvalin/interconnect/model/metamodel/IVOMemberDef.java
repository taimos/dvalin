package de.taimos.dvalin.interconnect.model.metamodel;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * IVO member definition
 */
@XmlType
public class IVOMemberDef extends MemberDef {

    private String ivoName;
    private Integer version;
    private String pkgName;


    /**
     * @return the ivoName
     */
    @XmlAttribute(required = true)
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
    @XmlAttribute(required = true)
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

}
