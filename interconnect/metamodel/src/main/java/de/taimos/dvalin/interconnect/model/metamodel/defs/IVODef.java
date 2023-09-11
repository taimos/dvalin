package de.taimos.dvalin.interconnect.model.metamodel.defs;

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

import de.taimos.dvalin.interconnect.model.metamodel.IGeneratorDefinition;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.BigDecimalMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.BooleanMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.CollectionMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.DateMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.EnumMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.IVOMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.ImplementsDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.IntegerMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.InterconnectObjectMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.LocalDateMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.LocalTimeMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.LongMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.MapMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.StringMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.UUIDMemberDef;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * IVO definition for the meta model
 */
@XmlRootElement(name = "ivo")
public class IVODef implements IGeneratorDefinition {

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
    private Boolean identity = false;
    private String filterPkgName;
    private String parentFilterPkgName;
    private Boolean auditing = false;
    private Boolean interfaceOnly = false;
    private Boolean pageable = false;
    private Boolean generateFindById = false;
    private Boolean generateFindByIdAudited = false;
    private Boolean generateDelete = false;
    private Boolean generateUpdate = false;
    private Boolean generateCreate = false;
    private Boolean generateSave = false;
    private Boolean generateFilter = false;
    private Boolean extendParentFilter = false;

    /**
     * @return the comment
     */
    @XmlAttribute(required = true)
    public String getComment() {
        return this.comment;
    }

    /**
     * @param comment the comment
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
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the children
     */
    @XmlElements({ //
        @XmlElement(name = "interconnectObject", type = InterconnectObjectMemberDef.class), //
        @XmlElement(name = "uuid", type = UUIDMemberDef.class), //
        @XmlElement(name = "integer", type = IntegerMemberDef.class), //
        @XmlElement(name = "map", type = MapMemberDef.class), //
        @XmlElement(name = "implements", type = ImplementsDef.class), //
        @XmlElement(name = "decimal", type = BigDecimalMemberDef.class), //
        @XmlElement(name = "boolean", type = BooleanMemberDef.class), //
        @XmlElement(name = "collection", type = CollectionMemberDef.class), //
        @XmlElement(name = "date", type = DateMemberDef.class), //
        @XmlElement(name = "enum", type = EnumMemberDef.class), //
        @XmlElement(name = "long", type = LongMemberDef.class), //
        @XmlElement(name = "ivo", type = IVOMemberDef.class), //
        @XmlElement(name = "string", type = StringMemberDef.class), //
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
    @XmlAttribute(required = false)
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
     * @return the parent name
     */
    @XmlAttribute(required = false)
    public String getParentName() {
        return this.parentName;
    }

    /**
     * @param parentName the parent name
     */
    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    /**
     * @return the parent version
     */
    @XmlAttribute(required = false)
    public Integer getParentVersion() {
        return this.parentVersion;
    }

    /**
     * @param parentVersion the parent version
     */
    public void setParentVersion(Integer parentVersion) {
        this.parentVersion = parentVersion;
    }

    /**
     * @return the identity
     */
    @XmlAttribute(required = false)
    public Boolean getIdentity() {
        return this.identity;
    }

    /**
     * @param identity the identity to set
     */
    public void setIdentity(Boolean identity) {
        this.identity = identity;
    }

    /**
     * @return the parentPkgName
     */
    @XmlAttribute(required = false)
    public String getParentPkgName() {
        return this.parentPkgName;
    }

    /**
     * @param parentPkgName the parentPkgName to set
     */
    public void setParentPkgName(String parentPkgName) {
        this.parentPkgName = parentPkgName;
    }

    /**
     * @return the filterPkgName
     */
    @XmlAttribute(required = false)
    public String getFilterPkgName() {
        return this.filterPkgName;
    }

    /**
     * @param filterPkgName the filterPkgName to set
     */
    public void setFilterPkgName(String filterPkgName) {
        this.filterPkgName = filterPkgName;
    }

    /**
     * @return the parentFilterPkgName
     */
    @XmlAttribute(required = false)
    public String getParentFilterPkgName() {
        return this.parentFilterPkgName;
    }

    /**
     * @param parentFilterPkgName the parentFilterPkgName to set
     */
    public void setParentFilterPkgName(String parentFilterPkgName) {
        this.parentFilterPkgName = parentFilterPkgName;
    }

    /**
     * @return the auditing
     */
    @XmlAttribute
    public Boolean getAuditing() {
        return this.auditing;
    }

    /**
     * @param auditing the auditing to set
     */
    public void setAuditing(Boolean auditing) {
        this.auditing = auditing;
    }

    /**
     * @return the interfaceOnly
     */
    @XmlAttribute
    public Boolean getInterfaceOnly() {
        return this.interfaceOnly;
    }

    /**
     * @param interfaceOnly the interfaceOnly to set
     */
    public void setInterfaceOnly(Boolean interfaceOnly) {
        this.interfaceOnly = interfaceOnly;
    }

    /**
     * @return the pageable
     */
    @XmlAttribute
    public Boolean getPageable() {
        return this.pageable;
    }

    /**
     * @param pageable the pageable to set
     */
    public void setPageable(Boolean pageable) {
        this.pageable = pageable;
    }

    /**
     * @return the removalDate, format as yyyy/mm/dd
     */
    @XmlAttribute(required = false)
    public String getRemovalDate() {
        return this.removalDate;
    }

    /**
     * @param removalDate the removalDate to set, format as yyyy/mm/dd
     */
    public void setRemovalDate(String removalDate) {
        this.removalDate = removalDate;
    }

    /**
     * @return the generateFindById
     */
    @XmlAttribute(required = false)
    public Boolean getGenerateFindById() {
        return this.generateFindById;
    }

    /**
     * @param generateFindById the generateFindById to set
     */
    public void setGenerateFindById(Boolean generateFindById) {
        this.generateFindById = generateFindById;
    }

    /**
     * @return the generateFindByIdAudited
     */
    public Boolean getGenerateFindByIdAudited() {
        return this.generateFindByIdAudited;
    }

    /**
     * @param generateFindByIdAudited the generateFindByIdAudited to set
     */
    public void setGenerateFindByIdAudited(Boolean generateFindByIdAudited) {
        this.generateFindByIdAudited = generateFindByIdAudited;
    }

    /**
     * @return the generateDelete
     */
    @XmlAttribute(required = false)
    public Boolean getGenerateDelete() {
        return this.generateDelete;
    }

    /**
     * @param generateDelete the generateDelete to set
     */
    public void setGenerateDelete(Boolean generateDelete) {
        this.generateDelete = generateDelete;
    }

    /**
     * @return the generateUpdate
     */
    @XmlAttribute(required = false)
    public Boolean getGenerateUpdate() {
        return this.generateUpdate;
    }

    /**
     * @param generateUpdate the generateUpdate to set
     */
    public void setGenerateUpdate(Boolean generateUpdate) {
        this.generateUpdate = generateUpdate;
    }

    /**
     * @return the generateCreate
     */
    @XmlAttribute(required = false)
    public Boolean getGenerateCreate() {
        return this.generateCreate;
    }

    /**
     * @param generateCreate the generateCreate to set
     */
    public void setGenerateCreate(Boolean generateCreate) {
        this.generateCreate = generateCreate;
    }

    /**
     * @return the generateFilter
     */
    @XmlAttribute(required = false)
    public Boolean getGenerateFilter() {
        return this.generateFilter;
    }

    /**
     * @param generateFilter the generateFilter to set
     */
    public void setGenerateFilter(Boolean generateFilter) {
        this.generateFilter = generateFilter;
    }

    /**
     * @return the generateSave
     */
    @XmlAttribute(required = false)
    public Boolean getGenerateSave() {
        return this.generateSave;
    }

    /**
     * @param generateSave the generateSave to set
     */
    public void setGenerateSave(Boolean generateSave) {
        this.generateSave = generateSave;
    }

    /**
     * @return the extendParentFilter
     */
    @XmlAttribute(required = false)
    public Boolean getExtendParentFilter() {
        return this.extendParentFilter;
    }

    /**
     * @param extendParentFilter the extendParentFilter to set
     */
    public void setExtendParentFilter(Boolean extendParentFilter) {
        this.extendParentFilter = extendParentFilter;
    }
}
