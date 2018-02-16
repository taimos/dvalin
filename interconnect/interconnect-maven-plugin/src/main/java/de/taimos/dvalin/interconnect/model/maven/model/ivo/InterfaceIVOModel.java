package de.taimos.dvalin.interconnect.model.maven.model.ivo;

import de.taimos.dvalin.interconnect.model.ivo.IIdentity;
import de.taimos.dvalin.interconnect.model.ivo.IVO;
import de.taimos.dvalin.interconnect.model.ivo.util.IIVOAuditing;
import de.taimos.dvalin.interconnect.model.maven.imports.ivo.IVOInterfaceImports;
import de.taimos.dvalin.interconnect.model.metamodel.defs.IVODef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.ImplementsDef;
import org.apache.maven.plugin.logging.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright 2018 Working-Horse<br>
 * <br>
 *
 * @author psigloch
 */
public class InterfaceIVOModel extends AbstractIVOModel {

    private static final String IVO_INTERFACE = "ivo/ivoInterface.vm";

    /**
     * @param definition the definition
     * @param logger     the logger
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
        result.put(this.getInterfaceClazzName(), InterfaceIVOModel.IVO_INTERFACE);
        return result;
    }

    protected void beforeChildHandling() {
        super.beforeChildHandling();
        this.definition.getChildren().add(this.getDefaultImplements());
        if(this.isAudited()) {
            this.definition.getChildren().add(this.getImplementsDef(IIVOAuditing.class));
        }
        if(this.isIdentity() && !this.hasParentClazz()) {
            this.definition.getChildren().add(this.getImplementsDef(IIdentity.class));
        }
    }

    private ImplementsDef getDefaultImplements() {
        if(this.hasParentClazz()) {
            ImplementsDef def = new ImplementsDef();
            def.setName(this.getParentInterfaceName());
            def.setPkgName(this.definition.getParentPkgName());
            return def;
        }
        return this.getImplementsDef(IVO.class);
    }


}
