package de.taimos.dvalin.interconnect.model.maven.model.ivo;

import de.taimos.dvalin.interconnect.model.ivo.util.IIVOAuditing;
import de.taimos.dvalin.interconnect.model.maven.imports.ivo.IVOInterfaceImports;
import de.taimos.dvalin.interconnect.model.ivo.IIdentity;
import de.taimos.dvalin.interconnect.model.metamodel.IVODef;
import de.taimos.dvalin.interconnect.model.metamodel.ImplementsDef;
import org.apache.maven.plugin.logging.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright 2018 Working-Horse<br>
 * <br>
 *
 * @author psigloch
 */
public class InterfaceIVOModel extends TemplateIVOModel {

    private static final String IVO_INTERFACE = "ivo/ivoInterface.vm";

    /**
     * @param definition the definition
     * @param logger the logger
     */
    public InterfaceIVOModel(IVODef definition, Log logger) {
        this.init(definition, new IVOInterfaceImports(), logger);
    }

    @Override
    protected boolean interfaceMode() {
        return true;
    }

    @Override
    public Map<String, String> generateClazzWithTemplates() {
        Map<String, String> result = new HashMap<>();
        result.put(this.definition.getIVOClazzName(true), InterfaceIVOModel.IVO_INTERFACE);
        return result;
    }

    @Override
    public String getParentClazzName() {
        return this.definition.getParentName() == null ? null : this.definition.getParentClazzName(true);
    }

    /**
     * velocity use
     * @return the interface implementations
     */
    public String getInterfaceImplements() {
        StringBuilder builder = new StringBuilder();

        if((this.getParentClazzName() != null)) {
            builder.append(", ");
            builder.append(this.getParentInterfaceName());
        }
        if(this.definition.getCompatibleBaseVersion() != null) {
            builder.append(", ");
            builder.append(this.definition.getIVOClazzName(true));
        }
        if(this.isAudited()) {
            builder.append(", ");
            builder.append(IIVOAuditing.class.getSimpleName());
        }
        if(this.isIdentity() && !this.hasParentClazz()) {
            builder.append(", ");
            builder.append(IIdentity.class.getSimpleName());
        }
        for(ImplementsDef i : this.implementsDef) {
            builder.append(", ");
            builder.append(i.getName());
        }

        if(builder.toString().trim().length() < 1) {
            return "";
        }

        return "extends " + builder.substring(2);
    }
}
