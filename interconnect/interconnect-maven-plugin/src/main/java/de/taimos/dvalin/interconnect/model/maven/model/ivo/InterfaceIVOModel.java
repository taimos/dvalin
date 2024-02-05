package de.taimos.dvalin.interconnect.model.maven.model.ivo;

import de.taimos.dvalin.interconnect.model.ivo.IIdentity;
import de.taimos.dvalin.interconnect.model.ivo.IPageable;
import de.taimos.dvalin.interconnect.model.ivo.IVO;
import de.taimos.dvalin.interconnect.model.ivo.util.IIVOAuditing;
import de.taimos.dvalin.interconnect.model.maven.GenerationContext;
import de.taimos.dvalin.interconnect.model.maven.imports.ivo.IVOInterfaceImports;
import de.taimos.dvalin.interconnect.model.maven.model.IAdditionalMemberHandler;
import de.taimos.dvalin.interconnect.model.maven.model.ivo.defs.PageableMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.defs.IVODef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.CollectionMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.ImplementsDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.MemberDef;
import org.apache.maven.plugin.logging.Log;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author psigloch
 */
public class InterfaceIVOModel extends AbstractIVOModel {

    private static final String IVO_INTERFACE = "ivo/ivoInterface.vm";

    /**
     * @param definition the definition
     * @param logger     the logger
     * @param additionalMemberHandlers the additional member handlers
     */
    public InterfaceIVOModel(IVODef definition, Log logger, IAdditionalMemberHandler... additionalMemberHandlers) {
        super(additionalMemberHandlers);
        this.init(definition, new IVOInterfaceImports(), logger);
    }

    @Override
    protected boolean interfaceMode() {
        return true;
    }

    @Override
    public List<MemberDef> getAllFields() {
        return super.getAllFields().stream().filter(memberDef -> Boolean.FALSE.equals(memberDef.getFilterOnly())).collect(Collectors.toList());
    }

    @Override
    public List<CollectionMemberDef> getCollectionFields() {
        return super.getCollectionFields().stream().filter(memberDef -> Boolean.FALSE.equals(memberDef.getFilterOnly())).collect(Collectors.toList());
    }

    @Override
    public List<MemberDef> getNoCollectionFields() {
        return super.getNoCollectionFields().stream().filter(memberDef -> Boolean.FALSE.equals(memberDef.getFilterOnly())).collect(Collectors.toList());
    }

    @Override
    public Collection<GenerationContext> getGenerationContexts() {
        Set<GenerationContext> result = new HashSet<>();
        result.add(new GenerationContext(InterfaceIVOModel.IVO_INTERFACE, this.getInterfaceClazzName(), true));
        return result;
    }

    @Override
    protected void beforeChildHandling() {
        super.beforeChildHandling();
        this.addChild(this.getDefaultInterfaceImplements());
        if (this.isAudited()) {
            this.addChild(new ImplementsDef(IIVOAuditing.class));
        }
        if (this.isIdentity() && !this.hasParentClazz()) {
            this.addChild(new ImplementsDef(IIdentity.class));
        }
        if (Boolean.TRUE.equals(this.definition.getPageable())) {
            this.addChild(new ImplementsDef(IPageable.class));
            this.addChildren(new PageableMemberDef());
        }
    }

    private ImplementsDef getDefaultInterfaceImplements() {
        if (this.hasParentClazz()) {
            ImplementsDef def = new ImplementsDef();
            def.setName(this.getParentInterfaceName());
            def.setPkgName(this.definition.getParentPkgName());
            return def;
        }
        return new ImplementsDef(IVO.class);
    }
}
