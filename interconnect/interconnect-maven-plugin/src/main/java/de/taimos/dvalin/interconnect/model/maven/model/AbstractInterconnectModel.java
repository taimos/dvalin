package de.taimos.dvalin.interconnect.model.maven.model;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import de.taimos.dvalin.interconnect.model.ivo.IVO;
import de.taimos.dvalin.interconnect.model.maven.imports.Imports;
import de.taimos.dvalin.interconnect.model.metamodel.IGeneratorDefinition;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.BigDecimalMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.CollectionMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.ContentDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.DateMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.EnumMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.IMultiMember;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.IVOMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.ImplementsDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.InterconnectObjectMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.LocalDateMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.LocalTimeMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.MapMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.MemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.UUIDMemberDef;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author psigloch
 */
public abstract class AbstractInterconnectModel<T extends IGeneratorDefinition, K extends Imports<T>> extends GeneratorModel<T, K> {

    protected final List<MemberDef> allMemberDefs = new ArrayList<>();
    protected final List<CollectionMemberDef> collectionMemberDefs = new ArrayList<>();
    protected final List<MemberDef> noCollectionMemberDefs = new ArrayList<>();
    protected final List<ImplementsDef> implementsDef = new ArrayList<>();
    protected final List<MapMemberDef> mapMemberDefs = new ArrayList<>();
    protected final List<IAdditionalMemberHandler> additionalMemberHandlers = new ArrayList<>();


    protected AbstractInterconnectModel(IAdditionalMemberHandler... additionalMemberHandlers) {
        if (additionalMemberHandlers != null) {
            this.additionalMemberHandlers.addAll(Arrays.asList(additionalMemberHandlers));
        }
    }

    protected boolean interfaceMode() {
        //override if needed
        return false;
    }

    @Override
    protected void beforeChildHandling() {
        super.beforeChildHandling();
        this.imports.initFromDefinition(this.definition, this);
        for (IAdditionalMemberHandler additionalMemberHandler : this.additionalMemberHandlers.stream().filter(IAdditionalMemberHandler::hasGlobalAdditions).collect(Collectors.toList())) {
            additionalMemberHandler.prepare(this.definition);
            this.imports.addAll(additionalMemberHandler.getGlobalImports(this.definition));
            this.implementsDef.addAll(additionalMemberHandler.getGlobalImplements(this.definition));
            this.addChildren(additionalMemberHandler.getGlobalChildren(this.definition));
        }
    }

    protected void handleChild(Object child) {
        if (child instanceof MemberDef) {
            if (child instanceof IMultiMember) {
                this.handleMultiMember((MemberDef) child);
            } else {
                this.handleSingleMember((MemberDef) child);
            }
            if (Boolean.TRUE.equals(((MemberDef) child).getJsonTransientFlag())) {
                this.imports.withJsonIgnore();
            }
            this.allMemberDefs.add((MemberDef) child);
        }
        if (child instanceof ImplementsDef) {
            this.handleImplementsDef((ImplementsDef) child);
        }
        for (IAdditionalMemberHandler additionalMemberHandler : this.additionalMemberHandlers.stream().filter(IAdditionalMemberHandler::isMemberDef).collect(Collectors.toSet())) {
            this.imports.addAll(additionalMemberHandler.getMemberImports(child));
        }

        this.handleMemberAdditionally(child);
    }

    protected void handleMemberAdditionally(Object member) {
        //nothing to, implement if needed
    }

    protected void handleImplementsDef(ImplementsDef member) {
        this.implementsDef.add(member);
        this.imports.add(member.getPkgName() + "." + member.getName());
    }

    protected void handleMultiMember(MemberDef member) {
        if (member instanceof CollectionMemberDef) {
            this.handleCollectionMembers((CollectionMemberDef) member);
            return;
        }
        if (member instanceof MapMemberDef) {
            this.handleMapMembers((MapMemberDef) member);
        }
    }


    protected void handleSingleMember(MemberDef member) {
        if (member instanceof InterconnectObjectMemberDef) {
            this.imports.add(((InterconnectObjectMemberDef) member).getPkgName() + "." + ((InterconnectObjectMemberDef) member).getClazz());
        }
        if (member instanceof IVOMemberDef) {
            if (((IVOMemberDef) member).getIvoName() == null) {
                this.imports.add(IVO.class.getCanonicalName());
            } else if ((((IVOMemberDef) member).getPkgName() != null) && !((IVOMemberDef) member).getPkgName().equals(this.definition.getPackageName())) {
                this.imports.add(((IVOMemberDef) member).getIVOPath(this.interfaceMode()));
            }
        }
        if (member instanceof EnumMemberDef) {
            this.imports.add(((EnumMemberDef) member).getPkgName() + "." + ((EnumMemberDef) member).getClazz());
        }
        if (member instanceof BigDecimalMemberDef) {
            this.imports.withBigDecimal();
        }
        if (member instanceof DateMemberDef) {
            if (Boolean.TRUE.equals(((DateMemberDef) member).getJodaMode())) {
                this.imports.withDateTime();
            } else {
                this.imports.add(ZonedDateTime.class);
            }
        }
        if (member instanceof LocalDateMemberDef) {
            this.imports.add(LocalDate.class);
        }
        if (member instanceof LocalTimeMemberDef) {
            this.imports.add(LocalTime.class);
        }
        if (member instanceof UUIDMemberDef) {
            this.imports.withUUID();
        }

        this.noCollectionMemberDefs.add(member);
    }


    protected void handleCollectionOrMapContentMembers(ContentDef content) {
        switch (content.getType()) {
            case Date:
                this.imports.add(DateTime.class);
                break;
            case ZonedDateTime:
                this.imports.add(ZonedDateTime.class);
                break;
            case LocalDate:
                this.imports.add(LocalDate.class);
                break;
            case LocalTime:
                this.imports.add(LocalTime.class);
                break;
            case Decimal:
                this.imports.add(BigDecimal.class);
                break;
            case InterconnectObject:
            case Enum:
                this.imports.add(content.getPkgName() + "." + content.getClazz());
                break;
            case IVO:
                if (content.getIvoName() == null) {
                    this.imports.add(IVO.class);
                } else {
                    this.imports.add(content.getPath(this.interfaceMode()));
                }
                break;
            default:
                Optional<IAdditionalMemberHandler> additionMemberHandler = this.additionalMemberHandlers.stream().filter(iamh -> iamh.isContentType(content.getType())).findFirst();
                additionMemberHandler.ifPresent(iAdditionalMemberHandler -> this.imports.addAll(iAdditionalMemberHandler.getMemberContentImports(content)));
                break;
        }
    }


    /**
     * @return whether the file should me removed or not
     */
    public boolean generateFile() {
        String dateString = this.definition.getRemovalDate();
        if (dateString == null || dateString.isEmpty()) {
            return true;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date removalDate;
        try {
            removalDate = format.parse(dateString);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Failed to parse the removal date - should be yyyy/MM/dd, is " + dateString);
        }
        return removalDate.compareTo(Calendar.getInstance().getTime()) > 0;
    }

    /**
     * @return the imports
     */
    public K getImports() {
        return this.imports;
    }

    /**
     * velocity use
     *
     * @return the interface implementations
     */
    public String getInterfaceImplements() {
        StringBuilder builder = new StringBuilder();
        for (ImplementsDef i : this.implementsDef) {
            builder.append(", ");
            builder.append(i.getName());
        }
        if (builder.toString().trim().isEmpty()) {
            return "";
        }
        return "extends " + builder.substring(2);
    }

    /**
     * @return all fields used within the ivo definition
     */
    public List<MemberDef> getAllFields() {
        return this.allMemberDefs;
    }

    /**
     * @return all fields used within the ivo definition which are of type Collection
     */
    public List<CollectionMemberDef> getCollectionFields() {
        return this.collectionMemberDefs;
    }

    /**
     * @return all fields used within the ivo definition which are neither of type Collection or Map
     */
    public List<MemberDef> getNoCollectionFields() {
        return this.allMemberDefs.stream().filter(md -> !(md instanceof CollectionMemberDef) && !(md instanceof MapMemberDef)).collect(Collectors.toList());
    }


    /**
     * @return all fields used within the ivo definition which are of type Map
     */
    public List<MapMemberDef> getMapFields() {
        return this.mapMemberDefs;
    }


    /**
     * @return the class name of the ivo
     */
    public abstract String getClazzName();

    /**
     * @return the class path of the ivo
     */
    public abstract String getClazzPath();

    /**
     * @return the interface clazz path
     */
    public String getInterfaceClazzPath() {
        return this.getClazzPath().replace(this.getClazzName(), this.getInterfaceClazzName());
    }

    /**
     * @return the interface class name of the ivo
     */
    public String getInterfaceClazzName() {
        return "I" + this.getClazzName();
    }

    /**
     * @return wheteher the ivo has a parent object or not
     */
    public boolean hasParentClazz() {
        return this.getParentClazzName() != null;
    }

    /**
     * @return the clazz name of the parent object, or null
     */
    public abstract String getParentClazzName();

    /**
     * @return the interface clazz name of the parent object, or null
     */
    public String getParentInterfaceName() {
        return this.hasParentClazz() ? "I" + this.getParentClazzName() : null;
    }

    /**
     * @return the path for the parent Clazz
     */
    public abstract String getParentClazzPath();

    /**
     * @return the interface clazz path
     */
    public String getParentInterfacePath() {
        return this.getParentClazzPath().replace(this.getParentClazzName(), this.getParentInterfaceName());
    }

    /**
     * @return the parent builder extends, or null
     */
    public String getParentBuilder() {
        return this.hasParentClazz() ? " extends Abstract" + this.getParentClazzName() + "Builder<E>" : "";
    }

    /**
     * @return true if the model is deprecated
     */
    public boolean isDeprecated() {
        return (this.definition.getRemovalDate() != null) && !this.definition.getRemovalDate().isEmpty();
    }

    /**
     * @return the package name
     */
    public String getPackageName() {
        return this.definition.getPackageName();
    }


    /**
     * @return the author
     */
    public String getAuthor() {
        return this.definition.getAuthor();
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return this.definition.getComment();
    }

    /**
     * @return the removedate as string
     */
    public String getRemoveDate() {
        return this.definition.getRemovalDate();
    }

    /**
     * @return the serial version from the defintion
     */
    public Integer getSerialVersion() {
        return this.definition.getVersion();
    }

    private void handleMapMembers(MapMemberDef member) {
        this.mapMemberDefs.add(member);
        switch (member.getMapType()) {
            case Map:
                this.imports.add(Map.class.getCanonicalName());
                if (!this.interfaceMode()) {
                    this.imports.add(HashMap.class.getCanonicalName());
                    this.imports.add(Collections.class.getCanonicalName());
                }
                break;
            case Multimap:
                if (!this.interfaceMode()) {
                    this.imports.add(Multimaps.class.getCanonicalName());
                }
                this.imports.add(Map.class.getCanonicalName());
                this.imports.add(Multimap.class.getCanonicalName());
                this.imports.add(HashMultimap.class.getCanonicalName());

                break;
            default:
                break;
        }
        this.handleCollectionOrMapContentMembers(member.getKeyContent());
        this.handleCollectionOrMapContentMembers(member.getValueContent());
    }

    private void handleCollectionMembers(CollectionMemberDef member) {
        this.collectionMemberDefs.add(member);
        this.imports.add(Collections.class.getCanonicalName());
        switch (member.getCollectionType()) {
            case Set:
                this.imports.add(Set.class.getCanonicalName());
                if (!this.interfaceMode()) {
                    this.imports.add(HashSet.class.getCanonicalName());
                }
                break;
            case List:
                this.imports.add(List.class.getCanonicalName());
                if (!this.interfaceMode()) {
                    this.imports.add(ArrayList.class.getCanonicalName());
                }
                break;
            default:
                break;
        }
        this.handleCollectionOrMapContentMembers(member.getContentDef());
    }

    /**
     * @param identifier the additional member handler identifier
     * @return the additional member handler or null
     */
    public IAdditionalMemberHandler getAdditionalHandler(String identifier) {
        if (identifier == null) {
            return null;
        }
        return this.additionalMemberHandlers.stream().filter(amh -> identifier.equalsIgnoreCase(amh.getIdentifier())).findFirst().orElse(null);
    }
}
