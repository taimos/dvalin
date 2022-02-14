package de.taimos.dvalin.interconnect.model.maven.model.ivo.modify;

import de.taimos.dvalin.interconnect.model.ivo.AbstractIVO;
import de.taimos.dvalin.interconnect.model.maven.GenerationContext;
import de.taimos.dvalin.interconnect.model.maven.imports.ivo.IVODeleteImports;
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
public class DeleteIVOModel extends AModifyModel {

    protected static final String VM = "ivo/delete.vm";
    protected static final String VM_INTERFACE = "ivo/deleteInterface.vm";

    /**
     * @param definition the definition
     * @param logger     the logger
     * @param additionalMemberHandlers additional member handlers
     */
    public DeleteIVOModel(IVODef definition, Log logger, IAdditionalMemberHandler... additionalMemberHandlers) {
        super(definition, new IVODeleteImports(), logger, additionalMemberHandlers);
    }

    @Override
    protected void handleChild(Object child) {
        //we just skip all childs, not needed for find by id
    }

    @Override
    protected Collection<GenerationContext> getInnerGenerationContexts() {
        Set<GenerationContext> result = new HashSet<>();
        if (Boolean.TRUE.equals(this.definition.getIdentity()) && Boolean.TRUE.equals(this.definition.getGenerateDelete())) {
            result.add(new GenerationContext(DeleteIVOModel.VM_INTERFACE, this.getFileName("Delete", true), true));
            result.add(new GenerationContext(DeleteIVOModel.VM, this.getFileName("Delete", false), false));
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

    @Override
    public String getParentClazzName() {
        return AbstractIVO.class.getSimpleName();
    }
}
