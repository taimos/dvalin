package de.taimos.dvalin.interconnect.model.maven.model.ivo.filter;

import de.taimos.dvalin.interconnect.model.maven.GenerationContext;
import de.taimos.dvalin.interconnect.model.maven.imports.ivo.IVOFilterByIdAuditedImports;
import de.taimos.dvalin.interconnect.model.maven.model.IAdditionalMemberHandler;
import de.taimos.dvalin.interconnect.model.maven.model.ivo.defs.PageableMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.defs.IVODef;
import org.apache.maven.plugin.logging.Log;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Copyright 2022 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public class FindByIdAuditedIVOModel extends AbstractFindModel {

    protected static final String VM = "ivo/findByIdAudited.vm";
    protected static final String VM_INTERFACE = "ivo/findByIdAuditedInterface.vm";

    private final PageableMemberDef pageableMembers;

    /**
     * @param definition the definition
     * @param logger     the logger
     * @param additionalMemberHandlers additional handlers
     */
    public FindByIdAuditedIVOModel(IVODef definition, Log logger, IAdditionalMemberHandler... additionalMemberHandlers) {
        super(definition, new IVOFilterByIdAuditedImports(), logger, additionalMemberHandlers);
        this.pageableMembers = new PageableMemberDef();
    }

    @Override
    protected Collection<GenerationContext> getInnerGenerationContexts() {
        Set<GenerationContext> result = new HashSet<>();
        if (Boolean.TRUE.equals(this.definition.getIdentity()) && Boolean.TRUE.equals(this.definition.getGenerateFindByIdAudited())) {
            result.add(new GenerationContext(FindByIdAuditedIVOModel.VM_INTERFACE, this.getFileName("ByIdAuditedIVO_v", true), true));
            result.add(new GenerationContext(FindByIdAuditedIVOModel.VM, this.getFileName("ByIdAuditedIVO_v", false), false));
        }
        return result;
    }

    @Override
    protected void beforeChildHandling() {
        super.beforeChildHandling();
        this.addChild(this.pageableMembers);
    }

    /**
     * @return the parent builder extends, or null
     */
    public String getParentBuilder() {
        return "";
    }

    /**
     * @return the filterable fields
     */
    public Collection<Object> getPageableMembers() {
        return this.pageableMembers;
    }
}
