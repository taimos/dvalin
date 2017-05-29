package de.taimos.dvalin.interconnect.model.maven;

/*
 * #%L
 * Dvalin interconnect maven plugin for source generation
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

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.beanutils.BeanUtils;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

import de.taimos.dvalin.interconnect.model.ToBeRemoved;
import de.taimos.dvalin.interconnect.model.ivo.AbstractIVO;
import de.taimos.dvalin.interconnect.model.ivo.Direction;
import de.taimos.dvalin.interconnect.model.ivo.IPageable;
import de.taimos.dvalin.interconnect.model.ivo.IPageableBuilder;
import de.taimos.dvalin.interconnect.model.ivo.IVO;
import de.taimos.dvalin.interconnect.model.ivo.IVOBuilder;
import de.taimos.dvalin.interconnect.model.ivo.util.IIVOAuditing;
import de.taimos.dvalin.interconnect.model.metamodel.BigDecimalMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.BooleanMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.CollectionMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.CollectionType;
import de.taimos.dvalin.interconnect.model.metamodel.ContentDef;
import de.taimos.dvalin.interconnect.model.metamodel.DateMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.EnumMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.FilterableType;
import de.taimos.dvalin.interconnect.model.metamodel.IFilterableMember;
import de.taimos.dvalin.interconnect.model.metamodel.ILabelMember;
import de.taimos.dvalin.interconnect.model.metamodel.IVODef;
import de.taimos.dvalin.interconnect.model.metamodel.IVOMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.ImplementsDef;
import de.taimos.dvalin.interconnect.model.metamodel.IntegerMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.InterconnectObjectMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.LongMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.MapMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.MapType;
import de.taimos.dvalin.interconnect.model.metamodel.MemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.StringMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.UUIDMemberDef;

/**
 * Utility functions for helping velocity
 */
public class MetaModelHelper {
    
    /**
     * the supported primitive types
     */
    public static final Set<String> SUPPORTED_PRIMITIVE_TYPES = Sets.newHashSet(BigDecimal.class.getSimpleName(), String.class.getSimpleName(), DateTime.class.getSimpleName(), Boolean.class.getSimpleName(), Long.class.getSimpleName());
    
    private final IVODef ivod;
    private final List<MemberDef> allMemberDefs = new ArrayList<>();
    private final List<MemberDef> noCollectionMemberDefs = new ArrayList<>();
    private final List<ImplementsDef> implementsDef = new ArrayList<>();
    private final List<EnumMemberDef> enumMemberDefs = new ArrayList<>();
    private final List<IVOMemberDef> ivoMemberDefs = new ArrayList<>();
    private final List<InterconnectObjectMemberDef> interconnectObjectMemberDefs = new ArrayList<>();
    private final List<CollectionMemberDef> collectionMemberDefs = new ArrayList<>();
    private final List<MapMemberDef> mapMemberDefs = new ArrayList<>();
    private final List<MemberDef> filterableMemberDefs = new ArrayList<>();
    private final List<MemberDef> entityLinkLabel = new ArrayList<>();
    private final Set<String> imports = new TreeSet<>();
    private final Set<String> iimports = new TreeSet<>();
    private final Set<String> fimports = new TreeSet<>();
    private FileType type = FileType.IVO;
    
    /**
     * @param ivod the ivo definition
     */
    public MetaModelHelper(IVODef ivod) {
        this.ivod = ivod;
        this.buildCache();
    }
    
    private void buildCache() {
        // sort children
        boolean hasDate = false;
        boolean hasBigDecimal = false;
        boolean hasUUID = false;
        if (this.ivod.getChildren() == null) {
            this.ivod.setChildren(new ArrayList<>());
        }
        if (this.ivod.getPageable()) {
            List<Object> children = new ArrayList<>(this.ivod.getChildren());
            children.add(this.createID(IPageable.class));
            children.add(this.createIMD("limit", "the maximum result size", true, FilterableType.none));
            children.add(this.createIMD("offset", "the offset of the first result", true, FilterableType.none));
            children.add(this.createSMD("sortBy", "provide this to enable a correct sorted paging of your lists. Use {@link #withSortDirection(Direction)} to provide information about sort direction", false, FilterableType.none));
            children.add(this.createEMD("sortDirection", Direction.class, "provide this to enable a correct sorted paging of your lists. Use {@link #withSortBy(String)} to provide information about the property to sort by", false, FilterableType.none));
            this.ivod.setChildren(children);
        }
        if (this.ivod.getGenerateFilter()) {
            this.filterableMemberDefs.add(this.createIMD("limit", "the maximum result size", true, FilterableType.single));
            this.filterableMemberDefs.add(this.createIMD("offset", "the offset of the first result", true, FilterableType.single));
            this.filterableMemberDefs.add(this.createSMD("sortBy", "provide this to enable a correct sorted paging of your lists. Use {@link #withSortDirection(Direction)} to provide information about sort direction", false, FilterableType.single));
            this.filterableMemberDefs.add(this.createEMD("sortDirection", Direction.class, "provide this to enable a correct sorted paging of your lists. Use {@link #withSortBy(String)} to provide information about the property to sort by", false, FilterableType.single));
        }
        
        for (Object o : this.ivod.getChildren()) {
            if ((o instanceof UUIDMemberDef) || (o instanceof BigDecimalMemberDef) || (o instanceof BooleanMemberDef) //
                || (o instanceof DateMemberDef) || (o instanceof IntegerMemberDef) || (o instanceof LongMemberDef) //
                || (o instanceof StringMemberDef)) {
                this.allMemberDefs.add((MemberDef) o);
                this.noCollectionMemberDefs.add((MemberDef) o);
            } else if (o instanceof CollectionMemberDef) {
                this.collectionMemberDefs.add((CollectionMemberDef) o);
            } else if (o instanceof EnumMemberDef) {
                this.enumMemberDefs.add((EnumMemberDef) o);
            } else if (o instanceof MapMemberDef) {
                this.mapMemberDefs.add((MapMemberDef) o);
            } else if (o instanceof InterconnectObjectMemberDef) {
                this.interconnectObjectMemberDefs.add((InterconnectObjectMemberDef) o);
            } else if (o instanceof IVOMemberDef) {
                this.ivoMemberDefs.add((IVOMemberDef) o);
            } else if (o instanceof ImplementsDef) {
                this.implementsDef.add((ImplementsDef) o);
            }
            
            if ((o instanceof ILabelMember) && ((ILabelMember) o).useAsLabel()) {
                this.entityLinkLabel.add((MemberDef) o);
            }
            
            if ((o instanceof IFilterableMember) && ((IFilterableMember) o).isAFilterMember()) {
                this.handleFilterable((MemberDef) o);
            }
            
            // import stuff
            if (o instanceof BigDecimalMemberDef) {
                hasBigDecimal = true;
            }
            if (o instanceof DateMemberDef) {
                hasDate = true;
            }
            if (o instanceof UUIDMemberDef) {
                hasUUID = true;
            }
        }
        this.allMemberDefs.addAll(this.collectionMemberDefs);
        this.allMemberDefs.addAll(this.enumMemberDefs);
        this.allMemberDefs.addAll(this.mapMemberDefs);
        this.allMemberDefs.addAll(this.ivoMemberDefs);
        this.allMemberDefs.addAll(this.interconnectObjectMemberDefs);
        this.noCollectionMemberDefs.addAll(this.enumMemberDefs);
        this.noCollectionMemberDefs.addAll(this.ivoMemberDefs);
        this.noCollectionMemberDefs.addAll(this.interconnectObjectMemberDefs);
        // determine imports
        this.imports.add(JsonDeserialize.class.getCanonicalName());
        this.imports.add(JsonPOJOBuilder.class.getCanonicalName());
        this.imports.add(Nullable.class.getCanonicalName());
        this.imports.add(IVOBuilder.class.getCanonicalName());
        this.iimports.add(Nullable.class.getCanonicalName());
        this.imports.add(IVO.class.getCanonicalName());
        this.imports.add(Nonnull.class.getCanonicalName());
        this.iimports.add(Nonnull.class.getCanonicalName());
        this.iimports.add(JsonTypeInfo.class.getCanonicalName());
        if (this.isDeprecated()) {
            this.imports.add(ToBeRemoved.class.getCanonicalName());
            this.iimports.add(ToBeRemoved.class.getCanonicalName());
        }
        if (this.ivod.getAuditing()) {
            this.iimports.add(IIVOAuditing.class.getCanonicalName());
            this.imports.add(DateTime.class.getCanonicalName());
        }
        if (this.ivod.getPageable()) {
            this.imports.add(IPageableBuilder.class.getCanonicalName());
            this.iimports.add(IPageableBuilder.class.getCanonicalName());
        }
        if (Boolean.TRUE.equals(this.ivod.getIdentity())) {
            this.iimports.add(JsonIgnore.class.getCanonicalName());
        }
        if ((this.ivod.getParentName() == null)) {
            this.imports.add(AbstractIVO.class.getCanonicalName());
        }
        if ((this.ivod.getFilterPkgName() != null) && !this.ivod.getFilterPkgName().equals(this.ivod.getPkgName())) {
            this.fimports.add(this.ivod.getPkgName() + "." + this.getIVOInterfaceName());
            this.fimports.add(this.ivod.getPkgName() + "." + this.getIVOClazzName());
        }
        if (this.ivod.getParentName() != null) {
            if ((this.ivod.getParentPkgName() != null) && !this.ivod.getParentPkgName().isEmpty() && !this.ivod.getPkgName().equals(this.ivod.getParentPkgName())) {
                this.fimports.add(this.ivod.getParentPkgName() + "." + this.getParentIVOFilterName() + "." + this.getParentIVONFName());
                this.imports.add(this.ivod.getParentPkgName() + "." + this.getVersionedClazzType(this.ivod.getParentName(), this.ivod.getParentVersion()));
                this.iimports.add(this.ivod.getParentPkgName() + "." + this.getVersionedType(this.ivod.getParentName(), this.ivod.getParentVersion(), true));
            }
            if ((this.ivod.getFilterPkgName() != null) && ((this.ivod.getParentFilterPkgName() == null) || this.ivod.getParentFilterPkgName().equals(this.ivod.getFilterPkgName()))) {
                this.fimports.add(this.ivod.getFilterPkgName() + "." + this.getParentIVOFilterName() + "." + this.getParentIVONFName());
            } else if (this.ivod.getFilterPkgName() != null) {
                this.fimports.add(this.ivod.getParentFilterPkgName() + "." + this.getParentIVOFilterName() + "." + this.getParentIVONFName());
            }
        }
        if (hasBigDecimal) {
            this.imports.add(BigDecimal.class.getCanonicalName());
            this.iimports.add(BigDecimal.class.getCanonicalName());
        }
        if (hasDate) {
            this.imports.add(DateTime.class.getCanonicalName());
            this.iimports.add(DateTime.class.getCanonicalName());
        }
        if (hasUUID) {
            this.imports.add(UUID.class.getCanonicalName());
            this.iimports.add(UUID.class.getCanonicalName());
        }
        for (EnumMemberDef emd : this.enumMemberDefs) {
            this.imports.add(emd.getPkgName() + "." + emd.getClazz());
            this.iimports.add(emd.getPkgName() + "." + emd.getClazz());
        }
        for (InterconnectObjectMemberDef iomd : this.interconnectObjectMemberDefs) {
            this.imports.add(iomd.getPkgName() + "." + iomd.getClazz());
            this.iimports.add(iomd.getPkgName() + "." + iomd.getClazz());
        }
        for (ImplementsDef id : this.implementsDef) {
            this.imports.add(id.getPkgName() + "." + id.getName());
            this.iimports.add(id.getPkgName() + "." + id.getName());
        }
        for (IVOMemberDef imd : this.ivoMemberDefs) {
            if (imd.getIvoName() == null) {
                this.imports.add(IVO.class.getCanonicalName());
                this.iimports.add(IVO.class.getCanonicalName());
            } else {
                if ((imd.getPkgName() != null) && !imd.getPkgName().equals(this.ivod.getPkgName())) {
                    this.imports.add(imd.getPkgName() + "." + this.getVersionedType(imd.getIvoName(), imd.getVersion(), false));
                    this.iimports.add(imd.getPkgName() + "." + this.getVersionedType(imd.getIvoName(), imd.getVersion(), true));
                }
                // if ((imd.getPkgName() != null) && (this.ivod.getFilterPkgName() != null) &&
                // !imd.getPkgName().equals(this.ivod.getFilterPkgName())) {
                // this.fimports.add(imd.getPkgName() + "." + this.getVersionedType(imd.getIvoName(), imd.getVersion()));
                // }
            }
        }
        for (CollectionMemberDef cmd : this.collectionMemberDefs) {
            this.imports.add(Collections.class.getCanonicalName());
            if (CollectionType.List.equals(cmd.getCollectionType())) {
                this.imports.add(List.class.getCanonicalName());
                this.iimports.add(List.class.getCanonicalName());
                this.imports.add(ArrayList.class.getCanonicalName());
            } else {
                this.imports.add(Set.class.getCanonicalName());
                this.iimports.add(Set.class.getCanonicalName());
                this.imports.add(HashSet.class.getCanonicalName());
            }
            this.addToImports(cmd.getContentDef());
        }
        for (MapMemberDef mmd : this.mapMemberDefs) {
            if (MapType.Map.equals(mmd.getMapType())) {
                this.imports.add(Map.class.getCanonicalName());
                this.imports.add(HashMap.class.getCanonicalName());
                this.iimports.add(Map.class.getCanonicalName());
                this.imports.add(Collections.class.getCanonicalName());
            } else {
                this.imports.add(Map.class.getCanonicalName());
                this.imports.add(Multimaps.class.getCanonicalName());
                this.imports.add(Multimap.class.getCanonicalName());
                this.imports.add(HashMultimap.class.getCanonicalName());
                this.iimports.add(Multimap.class.getCanonicalName());
            }
            this.addToImports(mmd.getKeyContent());
            this.addToImports(mmd.getValueContent());
        }
        for (MemberDef md : this.allMemberDefs) {
            if (Boolean.TRUE.equals(md.getJsonTransientFlag())) {
                this.imports.add(JsonIgnore.class.getCanonicalName());
            }
        }
        this.fimports.addAll(this.iimports);
        this.fimports.add(Collection.class.getCanonicalName());
    }
    
    private void handleFilterable(MemberDef o) {
        if (o.isAFilterMember()) {
            if (o instanceof DateMemberDef) {
                try {
                    DateMemberDef min = (DateMemberDef) BeanUtils.cloneBean(o);
                    DateMemberDef max = (DateMemberDef) BeanUtils.cloneBean(o);
                    min.setName(min.getName() + "Min");
                    max.setName(max.getName() + "Max");
                    this.filterableMemberDefs.add(min);
                    this.filterableMemberDefs.add(max);
                } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
                    this.filterableMemberDefs.add(o);
                }
            } else {
                this.filterableMemberDefs.add(o);
            }
        }
    }
    
    private ImplementsDef createID(Class<?> clazz) {
        ImplementsDef id = new ImplementsDef();
        id.setName(clazz.getSimpleName());
        id.setPkgName(clazz.getPackage().getName());
        return id;
    }
    
    private IntegerMemberDef createIMD(String name, String comment, boolean required, FilterableType filterableType) {
        IntegerMemberDef imd = new IntegerMemberDef();
        imd.setComment(comment);
        imd.setJavaTransientFlag(Boolean.FALSE);
        imd.setJsonTransientFlag(Boolean.FALSE);
        imd.setName(name);
        imd.setOrderTransient(Boolean.FALSE);
        imd.setRequired(required);
        imd.setFilterable(filterableType);
        return imd;
    }
    
    private LongMemberDef createLMD(String name, String comment, boolean required, FilterableType filterableType) {
        LongMemberDef lmd = new LongMemberDef();
        lmd.setComment(comment);
        lmd.setJavaTransientFlag(Boolean.FALSE);
        lmd.setJsonTransientFlag(Boolean.FALSE);
        lmd.setName(name);
        lmd.setOrderTransient(Boolean.FALSE);
        lmd.setRequired(required);
        lmd.setFilterable(filterableType);
        return lmd;
    }
    
    private StringMemberDef createSMD(String name, String comment, boolean required, FilterableType filterableType) {
        StringMemberDef smd = new StringMemberDef();
        smd.setComment(comment);
        smd.setJavaTransientFlag(Boolean.FALSE);
        smd.setJsonTransientFlag(Boolean.FALSE);
        smd.setName(name);
        smd.setOrderTransient(Boolean.FALSE);
        smd.setRequired(required);
        smd.setFilterable(filterableType);
        return smd;
    }
    
    private EnumMemberDef createEMD(String name, Class<?> clazz, String comment, boolean required, FilterableType filterableType) {
        EnumMemberDef emd = new EnumMemberDef();
        emd.setComment(comment);
        emd.setJavaTransientFlag(Boolean.FALSE);
        emd.setJsonTransientFlag(Boolean.FALSE);
        emd.setName(name);
        emd.setOrderTransient(Boolean.FALSE);
        emd.setRequired(required);
        emd.setClazz(clazz.getSimpleName());
        emd.setPkgName(clazz.getPackage().getName());
        emd.setFilterable(filterableType);
        return emd;
    }
    
    private void addToImports(ContentDef def) {
        switch (def.getType()) {
        case Date:
            this.imports.add(DateTime.class.getCanonicalName());
            this.iimports.add(DateTime.class.getCanonicalName());
            break;
        case Decimal:
            this.imports.add(BigDecimal.class.getCanonicalName());
            this.iimports.add(BigDecimal.class.getCanonicalName());
            break;
        case InterconnectObject:
        case Enum:
            this.imports.add(def.getPkgName() + "." + def.getClazz());
            this.iimports.add(def.getPkgName() + "." + def.getClazz());
            break;
        case IVO:
            if (def.getIvoName() == null) {
                this.imports.add(IVO.class.getCanonicalName());
                this.iimports.add(IVO.class.getCanonicalName());
            } else {
                if ((def.getPkgName() != null) && !def.getPkgName().equals(this.ivod.getPkgName())) {
                    this.imports.add(def.getPkgName() + "." + this.getVersionedType(def.getIvoName(), def.getVersion(), false));
                    this.iimports.add(def.getPkgName() + "." + this.getVersionedType(def.getIvoName(), def.getVersion(), true));
                }
                // if ((def.getPkgName() != null) && (this.ivod.getFilterPkgName() != null) &&
                // !def.getPkgName().equals(this.ivod.getFilterPkgName())) {
                // this.fimports.add(def.getPkgName() + "." + this.getVersionedType(def.getIvoName(), def.getVersion(), false));
                // }
            }
            break;
        default:
            break;
            
        }
    }
    
    /**
     * @return the real class name
     */
    public String getIVOClazzName() {
        return this.getRealVersionedType(this.ivod.getName());
    }
    
    /**
     * @return the modified class name
     */
    public String getModIVOClazzName() {
        switch (this.type) {
        case CREATE:
            return "Create" + this.getRealVersionedType(this.ivod.getName());
        case DELETE:
            return "Delete" + this.getRealVersionedType(this.ivod.getName());
        case FINDBY:
            return "Find" + this.ivod.getName() + "ByIdIVO_v" + this.ivod.getVersion();
        case IVO:
            return this.getRealVersionedType(this.ivod.getName());
        case UPDATE:
            return "Update" + this.getRealVersionedType(this.ivod.getName());
        case FILTER:
            return "Find" + this.getRealVersionedType(this.ivod.getName());
        case AUDITING:
            return "Find" + this.ivod.getName() + "ByIdAuditedIVO_v" + this.ivod.getVersion();
        default:
            return this.getRealVersionedType(this.ivod.getName());
        }
    }
    
    /**
     * @return the real interface name
     */
    public String getIVOInterfaceName() {
        return "I" + this.getIVOClazzName();
    }
    
    /**
     * @return the modified interface name
     */
    public String getModIVOInterfaceName() {
        return "I" + this.getModIVOClazzName();
    }
    
    /**
     * @return the real filter name
     */
    public String getIVOFilterName() {
        return this.ivod.getName() + "IVOFilter_v" + this.ivod.getVersion();
    }
    
    /**
     * @return the real filter name
     */
    public String getParentIVOFilterName() {
        return this.ivod.getParentName() + "IVOFilter_v" + this.ivod.getParentVersion();
    }
    
    /**
     * @return the real filter name
     */
    public String getIVODNFName() {
        return this.ivod.getName() + "IVODNF_v" + this.ivod.getVersion();
    }
    
    /**
     * @return the real filter name
     */
    public String getIVOCNFName() {
        return this.ivod.getName() + "IVOCNF_v" + this.ivod.getVersion();
    }
    
    /**
     * @return the real filter name
     */
    public String getIVONFName() {
        return this.ivod.getName() + "IVONF_v" + this.ivod.getVersion();
    }
    
    /**
     * @return the real filter name
     */
    public String getParentIVONFName() {
        return this.ivod.getParentName() + "IVONF_v" + this.ivod.getParentVersion();
    }
    
    /**
     * @return the implements string
     */
    public String getIImplements() {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        if ((this.ivod.getParentName() != null) && (this.type == FileType.IVO)) {
            builder.append("extends ");
            first = false;
            builder.append(this.getVersionedType(this.ivod.getParentName(), this.ivod.getParentVersion(), true));
            if (this.ivod.getCompatibleBaseVersion() != null) {
                builder.append(", ");
                builder.append(this.getVersionedType(this.ivod.getName(), this.ivod.getCompatibleBaseVersion(), true));
            }
        } else if (this.ivod.getCompatibleBaseVersion() != null) {
            builder.append("extends ");
            first = false;
            builder.append(this.getVersionedType(this.ivod.getName(), this.ivod.getCompatibleBaseVersion(), true));
        }
        if (this.ivod.getAuditing() && (this.type == FileType.IVO)) {
            if (first) {
                builder.append("extends ");
                first = false;
            } else {
                builder.append(", ");
            }
            builder.append("IIVOAuditing");
        }
        for (ImplementsDef i : this.implementsDef) {
            if (first) {
                builder.append("extends ");
                first = false;
            } else {
                builder.append(", ");
            }
            builder.append(i.getName());
        }
        if (this.type == FileType.FILTER) {
            if (first) {
                builder.append("extends ");
                first = false;
            } else {
                builder.append(", ");
            }
            builder.append(IPageable.class.getCanonicalName());
        }
        return builder.toString();
    }
    
    /**
     * @param buffer string to return with the first char in upper case
     * @return the upper-case-first string
     */
    public String upperCaseFirst(String buffer) {
        return buffer.length() > 0 ? buffer.substring(0, 1).toUpperCase() + buffer.substring(1) : "";
    }
    
    /**
     * @return the implementsDef
     */
    public List<ImplementsDef> getImplementsDef() {
        return this.implementsDef;
    }
    
    /**
     * @return the ivo member defs
     */
    public List<IVOMemberDef> getIvoMemberDefs() {
        return this.ivoMemberDefs;
    }
    
    /**
     * @return the enumMemberDefs
     */
    public List<EnumMemberDef> getEnumMemberDefs() {
        return this.enumMemberDefs;
    }
    
    /**
     * @return the MapMemberDefs
     */
    public List<MapMemberDef> getMapMemberDefs() {
        return this.mapMemberDefs;
    }
    
    /**
     * @return the collectionMemberDefs
     */
    public List<CollectionMemberDef> getCollectionMemberDefs() {
        return this.collectionMemberDefs;
    }
    
    /**
     * @return the allMemberDefs
     */
    public List<MemberDef> getAllMemberDefs() {
        return this.allMemberDefs;
    }
    
    /**
     * @return the allMemberDefs
     */
    public List<MemberDef> getEntitLinkLabels() {
        return this.entityLinkLabel;
    }
    
    /**
     * @return the allMemberDefs
     */
    public List<MemberDef> getFilterableMemberDefs() {
        return this.filterableMemberDefs;
    }
    
    /**
     * @return the no collection member defs
     */
    public List<MemberDef> getNoCollectionMemberDefs() {
        return this.noCollectionMemberDefs;
    }
    
    /**
     * @return the imports
     */
    public Set<String> getImports() {
        switch (this.type) {
        case IVO:
            Set<String> r = new HashSet<>();
            r.addAll(this.imports);
            r.add(JsonIgnore.class.getCanonicalName());
            return r;
        case FILTER:
            Set<String> r1 = new HashSet<>();
            r1.addAll(this.imports);
            r1.add(IPageable.class.getCanonicalName());
            r1.add(IPageableBuilder.class.getCanonicalName());
            r1.add(Direction.class.getCanonicalName());
            return r1;
        case CREATE:
        case UPDATE:
        case DELETE:
        case FINDBY:
        case AUDITING:
        default:
            return this.imports;
        }
    }
    
    /**
     * @return the imports for filters
     */
    public Set<String> getFImports() {
        return this.fimports;
    }
    
    /**
     * @param def the member
     * @return the type as string
     */
    public String getDefault(MemberDef def) {
        String result = "null";
        if (def instanceof CollectionMemberDef) {
            CollectionMemberDef cmd = (CollectionMemberDef) def;
            switch (cmd.getCollectionType()) {
            case List:
                result = "new ArrayList<>()";
                break;
            case Set:
                result = "new HashSet<>()";
                break;
            }
        } else if (def instanceof MapMemberDef) {
            MapMemberDef mmd = (MapMemberDef) def;
            switch (mmd.getMapType()) {
            case Map:
                result = "new HashMap<>()";
                break;
            case Multimap:
                result = "HashMultimap.create()";
                break;
            }
        }
        return result;
    }
    
    private String getRealVersionedType(String typeName) {
        return typeName + "IVO_v" + this.ivod.getVersion();
    }
    
    /**
     * @param typeName    the type name
     * @param version     the version
     * @param isInterface true if interface should be used
     * @return the type string
     */
    private String getVersionedType(String typeName, Integer version, boolean isInterface) {
        return (isInterface ? "I" : "") + typeName + "IVO_v" + version;
    }
    
    /**
     * @param typeName the type name
     * @param version  the version
     * @return the type string
     */
    public String getVersionedClazzType(String typeName, Integer version) {
        return typeName + "IVO_v" + version;
    }
    
    /**
     * @param content     the content
     * @param isInterface true if interface
     * @return the generic type
     */
    private String getGenericType(ContentDef content, boolean isInterface) {
        String result = null;
        switch (content.getType()) {
        case IVO:
            result = "? extends " + (content.getIvoName() == null ? "IVO" : this.getVersionedType(content.getIvoName(), content.getVersion(), isInterface));
            break;
        case Boolean:
            result = Boolean.class.getSimpleName();
            break;
        case Date:
            result = DateTime.class.getSimpleName();
            break;
        case Decimal:
            result = BigDecimal.class.getSimpleName();
            break;
        case Integer:
            result = Integer.class.getSimpleName();
            break;
        case Long:
            result = Long.class.getSimpleName();
            break;
        case String:
            result = String.class.getSimpleName();
            break;
        case InterconnectObject:
        case Enum:
            result = content.getClazz();
            break;
        case UUID:
            result = UUID.class.getSimpleName();
            break;
        }
        return result;
    }
    
    /**
     * @param def         the member
     * @param isInterface true if using interface mode
     * @return the type as string
     */
    public String getType(MemberDef def, boolean isInterface) {
        String result = null;
        if (def instanceof BigDecimalMemberDef) {
            result = BigDecimal.class.getSimpleName();
        } else if (def instanceof BooleanMemberDef) {
            result = Boolean.class.getSimpleName();
        } else if (def instanceof CollectionMemberDef) {
            CollectionMemberDef cmd = (CollectionMemberDef) def;
            StringBuilder builder = new StringBuilder();
            switch (cmd.getCollectionType()) {
            case List:
                builder.append(List.class.getSimpleName());
                break;
            case Set:
                builder.append(Set.class.getSimpleName());
                break;
            }
            builder.append("<");
            builder.append(this.getGenericType(cmd.getContentDef(), isInterface));
            builder.append(">");
            result = builder.toString();
        } else if (def instanceof DateMemberDef) {
            result = DateTime.class.getSimpleName();
        } else if (def instanceof EnumMemberDef) {
            result = ((EnumMemberDef) def).getClazz();
        } else if (def instanceof IntegerMemberDef) {
            result = Integer.class.getSimpleName();
        } else if (def instanceof InterconnectObjectMemberDef) {
            result = ((InterconnectObjectMemberDef) def).getClazz();
        } else if (def instanceof IVOMemberDef) {
            IVOMemberDef imd = (IVOMemberDef) def;
            result = imd.getIvoName() == null ? IVO.class.getSimpleName() : this.getVersionedType(imd.getIvoName(), imd.getVersion(), isInterface);
        } else if (def instanceof LongMemberDef) {
            result = Long.class.getSimpleName();
        } else if (def instanceof StringMemberDef) {
            result = String.class.getSimpleName();
        } else if (def instanceof MapMemberDef) {
            MapMemberDef mmd = (MapMemberDef) def;
            StringBuilder builder = new StringBuilder();
            switch (mmd.getMapType()) {
            case Map:
                builder.append(Map.class.getSimpleName());
                break;
            case Multimap:
                builder.append(Multimap.class.getSimpleName());
                break;
            }
            builder.append("<");
            builder.append(this.getGenericType(mmd.getKeyContent(), isInterface));
            builder.append(", ");
            builder.append(this.getGenericType(mmd.getValueContent(), isInterface));
            builder.append(">");
            result = builder.toString();
        } else if (def instanceof UUIDMemberDef) {
            result = UUID.class.getSimpleName();
        }
        return result;
    }
    
    /**
     * @return the interface imports
     */
    public Set<String> getIImports() {
        switch (this.type) {
        case FILTER:
            Set<String> r1 = new HashSet<>();
            r1.addAll(this.imports);
            r1.add(IPageable.class.getCanonicalName());
            r1.add(Direction.class.getCanonicalName());
            r1.add(JsonTypeInfo.class.getCanonicalName());
            return r1;
        default:
            return this.iimports;
        }
    }
    
    /**
     * @return the parent clazz
     */
    public String getParent() {
        switch (this.type) {
        case CREATE:
        case UPDATE:
        case DELETE:
        case FINDBY:
        case FILTER:
        case AUDITING:
            return AbstractIVO.class.getSimpleName();
        case IVO:
        default:
            return this.ivod.getParentName() == null ? AbstractIVO.class.getSimpleName() : this.getVersionedClazzType(this.ivod.getParentName(), this.ivod.getParentVersion());
        }
    }
    
    /**
     * @return the parent builder
     */
    public String getParentBuilder() {
        switch (this.type) {
        case CREATE:
        case UPDATE:
        case DELETE:
        case FINDBY:
        case FILTER:
        case AUDITING:
            return "";
        case IVO:
        default:
            return this.ivod.getParentName() == null ? "" : " extends Abstract" + this.getVersionedClazzType(this.ivod.getParentName(), this.ivod.getParentVersion()) + "Builder<E>";
        }
    }
    
    /**
     * @return true if ivo is deprecated
     */
    public boolean isDeprecated() {
        return (this.ivod.getRemovalDate() != null) && !this.ivod.getRemovalDate().isEmpty();
    }
    
    /**
     * @return the implements for the builder
     */
    public String getBuilderImplements() {
        StringBuilder result = new StringBuilder();
        if (this.ivod.getPageable() || (this.type == FileType.FILTER)) {
            result.append("implements ");
            if (this.ivod.getPageable() || (this.type == FileType.FILTER)) {
                result.append(IPageableBuilder.class.getSimpleName());
            }
        }
        return result.toString();
    }
    
    /**
     * @param type the type to set
     */
    public void setType(FileType type) {
        this.type = type;
    }
}
