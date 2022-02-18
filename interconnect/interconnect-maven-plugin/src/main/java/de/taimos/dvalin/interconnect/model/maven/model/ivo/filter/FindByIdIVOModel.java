package de.taimos.dvalin.interconnect.model.maven.model.ivo.filter;

import de.taimos.dvalin.interconnect.model.maven.GenerationContext;
import de.taimos.dvalin.interconnect.model.maven.imports.ivo.IVOFilterByIdImports;
import de.taimos.dvalin.interconnect.model.maven.model.IAdditionalMemberHandler;
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
public class FindByIdIVOModel extends AbstractFindModel {

    protected static final String VM = "ivo/findById.vm";
    protected static final String VM_INTERFACE = "ivo/findByIdInterface.vm";

    /**
     * @param definition the definition
     * @param logger     the logger
     * @param additionalMemberHandlers additional handlers
     */
    public FindByIdIVOModel(IVODef definition, Log logger, IAdditionalMemberHandler... additionalMemberHandlers) {
        super(definition, new IVOFilterByIdImports(), logger, additionalMemberHandlers);
    }

    @Override
    protected void handleChild(Object child) {
        //we just skip all childs, not needed for find by id
    }

    @Override
    protected Collection<GenerationContext> getInnerGenerationContexts() {
        Set<GenerationContext> result = new HashSet<>();
        if (Boolean.TRUE.equals(this.definition.getIdentity()) && Boolean.TRUE.equals(this.definition.getGenerateFindById())) {
            result.add(new GenerationContext(FindByIdIVOModel.VM_INTERFACE, this.getFileName("ByIdIVO_v", true), true));
            result.add(new GenerationContext(FindByIdIVOModel.VM, this.getFileName("ByIdIVO_v", false), false));
        }
        return result;
    }

    /**
     * @return the parent builder extends, or null
     */
    @Override
    public String getParentBuilder() {
        return "";
    }
}
