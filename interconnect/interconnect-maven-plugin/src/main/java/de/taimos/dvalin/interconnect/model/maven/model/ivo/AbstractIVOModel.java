package de.taimos.dvalin.interconnect.model.maven.model.ivo;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import de.taimos.dvalin.interconnect.model.ivo.IVO;
import de.taimos.dvalin.interconnect.model.maven.model.GeneratorModel;
import de.taimos.dvalin.interconnect.model.metamodel.ContentDef;
import de.taimos.dvalin.interconnect.model.metamodel.ILabelMember;
import de.taimos.dvalin.interconnect.model.metamodel.IMultiMember;
import de.taimos.dvalin.interconnect.model.metamodel.IVODef;
import de.taimos.dvalin.interconnect.model.metamodel.ImplementsDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.BigDecimalMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.CollectionMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.DateMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.EnumMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.IVOMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.InterconnectObjectMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.MapMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.MemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.UUIDMemberDef;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Copyright 2018 Working-Horse<br>
 * <br>
 *
 * @author psigloch
 */
public abstract class AbstractIVOModel extends GeneratorModel<IVODef> {

    protected final List<MemberDef> allMemberDefs = new ArrayList<>();
    protected final List<MemberDef> noCollectionMemberDefs = new ArrayList<>();
    protected final List<ImplementsDef> implementsDef = new ArrayList<>();
    protected final List<EnumMemberDef> enumMemberDefs = new ArrayList<>();
    protected final List<IVOMemberDef> ivoMemberDefs = new ArrayList<>();
    protected final List<InterconnectObjectMemberDef> interconnectObjectMemberDefs = new ArrayList<>();
    protected final List<CollectionMemberDef> collectionMemberDefs = new ArrayList<>();
    protected final List<MapMemberDef> mapMemberDefs = new ArrayList<>();
    protected final List<ILabelMember> labelMember = new ArrayList<>();

    protected boolean interfaceMode() {
        //override if needed
        return false;
    }

    protected void handleChild(Object child) {
        if(child instanceof MemberDef) {
            if(child instanceof IMultiMember) {
                this.handleMultiMember((MemberDef) child);
            } else {
                this.handleSingleMember((MemberDef) child);
            }
            if((child instanceof ILabelMember) && ((ILabelMember) child).useAsLabel()) {
                this.labelMember.add((ILabelMember) child);
            }
            if(Boolean.TRUE.equals(((MemberDef) child).getJsonTransientFlag())) {
                this.imports.withJsonIgnore();
            }
            this.allMemberDefs.add((MemberDef) child);
        }
        if(child instanceof ImplementsDef) {
            this.handleImplementsDef((ImplementsDef) child);
        }
        this.handleMemberAdditionaly(child);
    }


    protected void handleMemberAdditionaly(Object member) {
        //nothing to, implement if needed
    }

    protected void handleImplementsDef(ImplementsDef member) {
        this.implementsDef.add(member);
        this.imports.add(member.getPkgName() + "." + member.getName());
    }

    protected void handleMultiMember(MemberDef member) {
        if(member instanceof CollectionMemberDef) {
            this.collectionMemberDefs.add((CollectionMemberDef) member);
            this.imports.add(Collections.class.getCanonicalName());
            switch(((CollectionMemberDef) member).getCollectionType()) {
                case Set:
                    this.imports.add(Set.class.getCanonicalName());
                    if(!this.interfaceMode()) {
                        this.imports.add(HashSet.class.getCanonicalName());
                    }
                    break;
                case List:
                    this.imports.add(List.class.getCanonicalName());
                    if(!this.interfaceMode()) {
                        this.imports.add(ArrayList.class.getCanonicalName());
                    }
                    break;
            }
            this.handleContentMembers(((CollectionMemberDef) member).getContentDef());
            return;
        }
        if(member instanceof MapMemberDef) {
            this.mapMemberDefs.add((MapMemberDef) member);
            switch(((MapMemberDef) member).getMapType()) {
                case Map:
                    this.imports.add(Map.class.getCanonicalName());
                    if(!this.interfaceMode()) {
                        this.imports.add(HashMap.class.getCanonicalName());
                        this.imports.add(Collections.class.getCanonicalName());
                    }
                    break;
                case Multimap:
                    if(!this.interfaceMode()) {
                        this.imports.add(Multimaps.class.getCanonicalName());
                    }
                    this.imports.add(Map.class.getCanonicalName());
                    this.imports.add(Multimap.class.getCanonicalName());
                    this.imports.add(HashMultimap.class.getCanonicalName());

                    break;
            }
            this.handleContentMembers(((MapMemberDef) member).getKeyContent());
            this.handleContentMembers(((MapMemberDef) member).getValueContent());
        }
    }

    protected void handleSingleMember(MemberDef member) {
        if(member instanceof InterconnectObjectMemberDef) {
            this.interconnectObjectMemberDefs.add((InterconnectObjectMemberDef) member);
            this.imports.add(((InterconnectObjectMemberDef) member).getPkgName() + "." + ((InterconnectObjectMemberDef) member).getClazz());
        }
        if(member instanceof IVOMemberDef) {
            this.ivoMemberDefs.add((IVOMemberDef) member);
            if(((IVOMemberDef) member).getIvoName() == null) {
                this.imports.add(IVO.class.getCanonicalName());
            } else if((((IVOMemberDef) member).getPkgName() != null) && !((IVOMemberDef) member).getPkgName().equals(this.definition.getPackageName())) {
                this.imports.add(((IVOMemberDef) member).getIVOPath(this.interfaceMode()));
            }
        }
        if(member instanceof EnumMemberDef) {
            this.enumMemberDefs.add((EnumMemberDef) member);
            this.imports.add(((EnumMemberDef) member).getPkgName() + "." + ((EnumMemberDef) member).getClazz());
        }
        if(member instanceof BigDecimalMemberDef) {
            this.imports.withBigDecimal();
        }
        if(member instanceof DateMemberDef) {
            this.imports.withDateTime();
        }
        if(member instanceof UUIDMemberDef) {
            this.imports.withUUID();
        }

        this.noCollectionMemberDefs.add(member);
    }


    protected void handleContentMembers(ContentDef content) {
        switch(content.getType()) {
            case Date:
                this.imports.with(DateTime.class);
                break;
            case Decimal:
                this.imports.with(BigDecimal.class);
                break;
            case InterconnectObject:
            case Enum:
                this.imports.with(content.getPkgName() + "." + content.getClazz());
                break;
            case IVO:
                if(content.getIvoName() == null) {
                    this.imports.with(IVO.class);
                } else {
                    this.imports.with(content.getPath(this.interfaceMode()));
                }
                break;
        }
    }
}
