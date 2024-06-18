package de.taimos.dvalin.interconnect.model.maven.model.ivo.filter;

import de.taimos.dvalin.interconnect.model.ivo.AbstractIVO;
import de.taimos.dvalin.interconnect.model.ivo.IIdentity;
import de.taimos.dvalin.interconnect.model.ivo.IPageable;
import de.taimos.dvalin.interconnect.model.ivo.IVO;
import de.taimos.dvalin.interconnect.model.maven.GenerationContext;
import de.taimos.dvalin.interconnect.model.maven.imports.ivo.IVOFilterImports;
import de.taimos.dvalin.interconnect.model.maven.model.IAdditionalMemberHandler;
import de.taimos.dvalin.interconnect.model.metamodel.defs.IVODef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.ContentDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.ContentType;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.DateMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.FilterableType;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.IVOMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.ImplementsDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.LocalDateMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.LocalTimeMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.MemberDef;
import org.apache.maven.plugin.logging.Log;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Copyright 2022 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public class FindInterfaceModel extends AbstractFindModel {

    protected static final String VM_INTERFACE = "ivo/findInterface.vm";

    private static final String TYPE = "IVO_v";

    /**
     * @param definition               the definition
     * @param logger                   the logger
     * @param additionalMemberHandlers additional handlers
     */
    public FindInterfaceModel(IVODef definition, Log logger, IAdditionalMemberHandler... additionalMemberHandlers) {
        super(definition, new IVOFilterImports(), logger, additionalMemberHandlers);
    }

    @Override
    protected Collection<GenerationContext> getInnerGenerationContexts() {
        Set<GenerationContext> result = new HashSet<>();
        if (Boolean.TRUE.equals(this.definition.getGenerateFilter())) {
            result.add(new GenerationContext(FindInterfaceModel.VM_INTERFACE, this.getFileName(FindInterfaceModel.TYPE, true), true));
        }
        return result;
    }

    @Override
    protected void handleMemberAdditionally(Object member) {
        super.handleMemberAdditionally(member);
        if (member instanceof IVOMemberDef) {
            this.imports.add(((IVOMemberDef) member).getIVOPath(false));
            this.imports.add(((IVOMemberDef) member).getIVOPath(true));
        }
        if (member instanceof MemberDef && ((MemberDef) member).getFilterable() == FilterableType.multi) {
            this.imports.add(Set.class);
        }
    }



    @Override
    protected void handleCollectionOrMapContentMembers(ContentDef content) {
        super.handleCollectionOrMapContentMembers(content);
        if (content.getType() == ContentType.IVO && content.getIvoName() != null) {
            this.imports.add(content.getPath(true));
        }
    }

    /**
     * @return the filterable fields
     */
    public Collection<Object> getFilterableFields() {
        return this.modifiableChildren.stream().filter(MemberDef.class::isInstance).filter(md -> ((MemberDef) md).isAFilterMember()).collect(Collectors.toList());
    }

    @Override
    protected void beforeChildHandling() {
        super.beforeChildHandling();
        if (this.hasParentClazz()) {
            this.imports.add(this.definition.getParentFilterPkgName() + "." + this.getParentFilterClazzName(true));
            this.imports.remove(this.definition.getParentFilterPkgName() + "." + AbstractIVO.class.getSimpleName());
            this.implementsDef.add(new ImplementsDef(this.getParentFilterClazzName(true), this.definition.getParentFilterPkgName()));
        } else {
            this.addChild(new ImplementsDef(IPageable.class));
            this.addChild(new ImplementsDef(IVO.class));
        }

        for (Object child : this.modifiableChildren.stream().filter(c -> c instanceof DateMemberDef || c instanceof LocalDateMemberDef || c instanceof LocalTimeMemberDef).filter(md -> ((MemberDef) md).getFilterable() == FilterableType.single).collect(Collectors.toSet())) {
            this.createMinMaxMember((MemberDef) child);
            this.modifiableChildren.remove(child);
        }
    }

    @Override
    public String getInterfaceImplements() {
        String interfaces = this.implementsDef.stream().filter(i -> !i.getName().equalsIgnoreCase(IIdentity.class.getSimpleName())) //
            .filter(i -> !i.getName().equalsIgnoreCase(IIdentity.class.getSimpleName())) //
            .filter(i -> !i.getName().equalsIgnoreCase("I" + super.getParentClazzName())) //
            .filter(i -> Boolean.FALSE.equals(i.getSkipOnFilter()))//
            .map(ImplementsDef::getName).collect(Collectors.joining(", "));

        if (interfaces.isEmpty()) {
            return "";
        }

        return "extends " + interfaces;
    }

    /**
     * @return whether the ivo has a parent object or not
     */
    @Override
    public boolean hasParentClazz() {
        return this.definition.getExtendParentFilter() && this.definition.getParentFilterPkgName() != null && this.definition.getParentName() != null;
    }

    @Override
    public String getParentClazzName() {
        if (!this.hasParentClazz()) {
            return AbstractIVO.class.getSimpleName();
        }
        return this.getParentFilterClazzName(false);
    }

    @Override
    public String getParentInterfaceName() {
        return AbstractIVO.class.getSimpleName();
    }

    @Override
    public String getParentClazzPath() {
        return this.definition.getParentFilterPkgName() + "." + this.getParentClazzName();
    }

    private String getParentFilterClazzName(boolean isInterface) {
        return (isInterface ? "I" : "") + "Find" + this.definition.getParentName() + FindInterfaceModel.TYPE + this.definition.getParentVersion();
    }
}
