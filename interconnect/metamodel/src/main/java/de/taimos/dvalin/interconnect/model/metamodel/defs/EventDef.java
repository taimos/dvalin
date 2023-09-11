package de.taimos.dvalin.interconnect.model.metamodel.defs;

import de.taimos.dvalin.interconnect.model.metamodel.IGeneratorDefinition;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.BigDecimalMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.BooleanMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.CollectionMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.DateMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.EnumMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.ImplementsDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.IntegerMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.LocalDateMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.LocalTimeMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.LongMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.MapMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.StringMemberDef;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author psigloch
 */
@XmlRootElement(name = "event")
public class EventDef implements IGeneratorDefinition {
    private String comment;
    private String name;
    private String removalDate;
    private List<Object> children;
    private Integer version;
    private Integer compatibleBaseVersion;
    private String pkgName;
    private String author;
    private String parentName;
    private Integer parentVersion;
    private String parentPkgName;
    private String domain;

    /**
     * @return the comment
     */
    @XmlAttribute(required = true)
    public String getComment() {
        return this.comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return the name
     */
    @XmlAttribute(required = true)
    public String getName() {
        return this.name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the removalDate
     */
    @XmlAttribute
    public String getRemovalDate() {
        return this.removalDate;
    }

    /**
     * @param removalDate the removalDate to set
     */
    public void setRemovalDate(String removalDate) {
        this.removalDate = removalDate;
    }

    /**
     * @return the children
     */
    @XmlElements({//
        @XmlElement(name = "integer", type = IntegerMemberDef.class), //
        @XmlElement(name = "implements", type = ImplementsDef.class), //
        @XmlElement(name = "decimal", type = BigDecimalMemberDef.class), //
        @XmlElement(name = "boolean", type = BooleanMemberDef.class), //
        @XmlElement(name = "date", type = DateMemberDef.class), //
        @XmlElement(name = "enum", type = EnumMemberDef.class), //
        @XmlElement(name = "long", type = LongMemberDef.class), //
        @XmlElement(name = "string", type = StringMemberDef.class), //
        @XmlElement(name = "map", type = MapMemberDef.class), //
        @XmlElement(name = "collection", type = CollectionMemberDef.class), //
        @XmlElement(name = "localDate", type = LocalDateMemberDef.class), //
        @XmlElement(name = "localTime", type = LocalTimeMemberDef.class)})
    public List<Object> getChildren() {
        return this.children;
    }

    /**
     * @param children the children to set
     */
    public void setChildren(List<Object> children) {
        this.children = children;
    }

    @Override
    public String getPackageName() {
        return this.getPkgName();
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
     * @return the compatibleBaseVersion
     */
    @XmlAttribute
    public Integer getCompatibleBaseVersion() {
        return this.compatibleBaseVersion;
    }

    /**
     * @param compatibleBaseVersion the compatibleBaseVersion to set
     */
    public void setCompatibleBaseVersion(Integer compatibleBaseVersion) {
        this.compatibleBaseVersion = compatibleBaseVersion;
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

    /**
     * @return the author
     */
    @XmlAttribute(required = true)
    public String getAuthor() {
        return this.author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return the domain
     */
    @XmlAttribute(required = true)
    public String getDomain() {
        return this.domain;
    }

    /**
     * @param domain the domain to set
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * @return the parentName
     */
    @XmlAttribute
    public String getParentName() {
        return this.parentName;
    }

    /**
     * @param parentName the parentName to set
     */
    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    /**
     * @return the parentVersion
     */
    @XmlAttribute
    public Integer getParentVersion() {
        return this.parentVersion;
    }

    /**
     * @param parentVersion the parentVersion to set
     */
    public void setParentVersion(Integer parentVersion) {
        this.parentVersion = parentVersion;
    }

    /**
     * @return the parentPkgName
     */
    @XmlAttribute
    public String getParentPkgName() {
        return this.parentPkgName;
    }

    /**
     * @param parentPkgName the parentPkgName to set
     */
    public void setParentPkgName(String parentPkgName) {
        this.parentPkgName = parentPkgName;
    }
}
