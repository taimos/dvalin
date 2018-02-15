package de.taimos.dvalin.interconnect.model.maven.model.ivo;

import de.taimos.dvalin.interconnect.model.ivo.AbstractIVO;
import de.taimos.dvalin.interconnect.model.maven.GeneratorHelper;
import de.taimos.dvalin.interconnect.model.maven.imports.ivo.IVOImports;
import de.taimos.dvalin.interconnect.model.metamodel.IVODef;
import org.apache.maven.plugin.logging.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright 2018 Working-Horse<br>
 * <br>
 *
 * @author psigloch
 */
public class IVOModel extends TemplateIVOModel {
    private static final String IVO = "ivo/ivo.vm";

    /**
     * @param definition the definition
     * @param logger the logger
     */
    public IVOModel(IVODef definition, Log logger) {
        this.init(definition, new IVOImports(), logger);
    }

    @Override
    public Map<String, String> generateClazzWithTemplates() {
        if(Boolean.TRUE.equals(this.definition.getInterfaceOnly())) {
            return null;
        }
        Map<String, String> result = new HashMap<>();
        if((this.definition.getRemovalDate() == null) || this.definition.getRemovalDate().isEmpty() || GeneratorHelper.keepGeneratedFiles(this.definition.getRemovalDate())) {
            result.put(this.definition.getIVOClazzName(false), IVOModel.IVO);
        } else if(this.getLogger() != null) {
            this.getLogger().info(this.definition.getIVOClazzName(false) + " is beyond removal date, only the interface is generated.");
        }
        return result;
    }

    @Override
    public String getParentClazzName() {
        return this.definition.getParentName() == null ? AbstractIVO.class.getSimpleName() : this.definition.getParentClazzName(false);
    }

    @Override
    public String getParentInterfaceName() {
        return this.definition.getParentName() == null ? AbstractIVO.class.getSimpleName() : this.definition.getParentClazzName(true);
    }

    /**
     * velocity use
     * @return provides ivo end addition
     */
    public boolean hasIVOEndAddition() {
        return false;
    }

    /**
     * velocity use
     * @return provides ivo end addition path, relative to resources/ivo
     */
    public String getIVOEndAddition() {
        return "";
    }
}
