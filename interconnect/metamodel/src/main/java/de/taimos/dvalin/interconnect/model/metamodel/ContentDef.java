package de.taimos.dvalin.interconnect.model.metamodel;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Content definition for tables, maps and collections
 */
@XmlType
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
}
