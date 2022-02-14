package de.taimos.dvalin.interconnect.model.maven.model.ivo.modify;

import de.taimos.dvalin.interconnect.model.maven.GenerationContext;
import de.taimos.dvalin.interconnect.model.maven.imports.ivo.IVOModifyImports;
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
public class SaveIVOModel extends AModifyModel {

    protected static final String VM = "ivo/modify.vm";
    protected static final String VM_INTERFACE = "ivo/modifyInterface.vm";

    /**
     * @param definition teh ivo definition
     * @param logger the logger
     * @param additionalMemberHandlers additional member handlers
     */
    public SaveIVOModel(IVODef definition, Log logger, IAdditionalMemberHandler... additionalMemberHandlers) {
        super(definition, new IVOModifyImports(), logger, additionalMemberHandlers);
    }

    @Override
    protected Collection<GenerationContext> getInnerGenerationContexts() {
        Set<GenerationContext> result = new HashSet<>();
        if (Boolean.TRUE.equals(this.definition.getGenerateSave())) {
            result.add(new GenerationContext(SaveIVOModel.VM_INTERFACE, this.getFileName("Save", true), true));
            result.add(new GenerationContext(SaveIVOModel.VM, this.getFileName("Save", false), false));
        }
        return result;
    }
}
