package de.taimos.dvalin.interconnect.model.maven.model.ivo.filter;

import de.taimos.dvalin.interconnect.model.ivo.AbstractIVO;
import de.taimos.dvalin.interconnect.model.maven.GenerationContext;
import de.taimos.dvalin.interconnect.model.maven.imports.ivo.BaseIVOImports;
import de.taimos.dvalin.interconnect.model.maven.model.IAdditionalMemberHandler;
import de.taimos.dvalin.interconnect.model.maven.model.ivo.AbstractIVOModel;
import de.taimos.dvalin.interconnect.model.metamodel.defs.IVODef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.FilterableType;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.MemberDef;
import org.apache.maven.plugin.logging.Log;
import org.springframework.beans.BeanUtils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;

/**
 * Copyright 2022 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public abstract class AbstractFindModel extends AbstractIVOModel {

    protected AbstractFindModel(IVODef definition, BaseIVOImports imports, Log logger, IAdditionalMemberHandler... additionalMemberHandlers) {
        super(additionalMemberHandlers);
        this.init(definition, imports, logger);
    }

    @Override
    public String getTargetFolder() {
        return super.getTargetFolder() + File.separator + "requests";
    }

    @Override
    public Collection<GenerationContext> getGenerationContexts() {
        if (Boolean.TRUE.equals(this.definition.getInterfaceOnly())) {
            return Collections.emptySet();
        }
        if (!this.generateFile()) {
            return Collections.emptySet();
        }
        return this.getInnerGenerationContexts();
    }


    protected abstract Collection<GenerationContext> getInnerGenerationContexts();

    protected String getFileName(String typeName, boolean isInterface) {
        return (isInterface ? "I" : "") + "Find" + this.definition.getName() + typeName + this.definition.getVersion();
    }

    @Override
    public String getParentClazzName() {
        return AbstractIVO.class.getSimpleName();
    }

    protected void createMinMaxMember(MemberDef member) {
		MemberDef min = BeanUtils.instantiateClass(member.getClass());
		BeanUtils.copyProperties(member, min);
		min.setName(min.getName() + "Min");
		min.setFilterable(FilterableType.single);
		this.addChild(min);
		MemberDef max = BeanUtils.instantiateClass(member.getClass());
		BeanUtils.copyProperties(member, max);
		max.setName(max.getName() + "Max");
		max.setFilterable(FilterableType.single);
		this.addChild(max);
	}
}
