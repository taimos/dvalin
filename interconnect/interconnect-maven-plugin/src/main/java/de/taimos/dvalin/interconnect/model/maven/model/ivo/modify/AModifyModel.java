package de.taimos.dvalin.interconnect.model.maven.model.ivo.modify;

import de.taimos.dvalin.interconnect.model.ivo.AbstractIVO;
import de.taimos.dvalin.interconnect.model.maven.GenerationContext;
import de.taimos.dvalin.interconnect.model.maven.imports.ivo.BaseIVOImports;
import de.taimos.dvalin.interconnect.model.maven.model.IAdditionalMemberHandler;
import de.taimos.dvalin.interconnect.model.maven.model.ivo.AbstractIVOModel;
import de.taimos.dvalin.interconnect.model.metamodel.defs.IVODef;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

/**
 * Copyright 2022 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public abstract class AModifyModel extends AbstractIVOModel {

    protected AModifyModel(IVODef definition, BaseIVOImports imports, Log logger, IAdditionalMemberHandler... additionalMemberHandlers) {
        super(additionalMemberHandlers);
        this.init(definition, imports, logger);

    }

    protected abstract Collection<GenerationContext> getInnerGenerationContexts();

    @Override
    public String getTargetFolder() {
        return super.getTargetFolder() + File.separator + "requests";
    }


    @Override
    public Collection<GenerationContext>  getGenerationContexts() {
        if (Boolean.TRUE.equals(this.definition.getInterfaceOnly())) {
            return Collections.emptySet();
        }
        if (!this.generateFile()) {
            return Collections.emptySet();
        }
        return this.getInnerGenerationContexts();
    }

    @Override
    protected void beforeChildHandling() {
        super.beforeChildHandling();
        this.imports.add(this.getClazzPath());
    }

    protected String getFileName(String prefix, boolean isInterface) {
        return (isInterface ? "I" : "") + prefix + this.definition.getName() + "IVO_v" + this.definition.getVersion();
    }

    /**
     * @return wheteher the ivo has a parent object or not
     */
    @Override
    public boolean hasParentClazz() {
        return this.definition.getParentName() != null;
    }

    @Override
    public String getParentClazzName() {
        return this.definition.getParentName() == null ? AbstractIVO.class.getSimpleName() : super.getParentClazzName();
    }

    @Override
    public String getParentInterfaceName() {
        return this.definition.getParentName() == null ? AbstractIVO.class.getSimpleName() : super.getParentInterfaceName();
    }

}
