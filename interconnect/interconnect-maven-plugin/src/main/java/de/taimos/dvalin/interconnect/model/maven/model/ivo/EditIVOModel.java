package de.taimos.dvalin.interconnect.model.maven.model.ivo;

import de.taimos.dvalin.interconnect.model.ivo.AbstractIVO;
import de.taimos.dvalin.interconnect.model.maven.imports.ivo.IVOFilterImports;
import de.taimos.dvalin.interconnect.model.metamodel.defs.IVODef;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright 2018 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public class EditIVOModel extends AbstractIVOModel {

    private static final String FIND_BY = "ivo/findBy.vm";
    private static final String FIND_BY_INTERFACE = "ivo/findByInterface.vm";
    private static final String EDIT = "ivo/edit.vm";
    private static final String EDIT_INTERFACE = "ivo/editInterface.vm";

    /**
     * @param definition the definition
     * @param logger     the logger
     */
    public EditIVOModel(IVODef definition, Log logger) {
        this.init(definition, new IVOFilterImports(), logger);

    }

    @Override
    public String getTargetFolder() {
        return super.getTargetFolder() + File.separator + "requests";
    }

    @Override
    public Map<String, String> generateClazzWithTemplates() {
        if(Boolean.TRUE.equals(this.definition.getInterfaceOnly())) {
            return null;
        }
        if(this.genereateFile()) {
            Map<String, String> result = new HashMap<>();

            if(this.definition.getGenerateSave()) {
                result.put(this.getFileName("Save", true), EditIVOModel.EDIT_INTERFACE);
                result.put(this.getFileName("Save", false), EditIVOModel.EDIT);
            } else {
                if(this.definition.getGenerateCreate()) {
                    result.put(this.getFileName("Create", true), EditIVOModel.EDIT_INTERFACE);
                    result.put(this.getFileName("Create", false), EditIVOModel.EDIT);
                }

                if(this.definition.getGenerateUpdate()) {
                    result.put(this.getFileName("Update", true), EditIVOModel.EDIT_INTERFACE);
                    result.put(this.getFileName("Update", false), EditIVOModel.EDIT);
                }
            }
            if(this.definition.getGenerateDelete()) {
                result.put(this.getFileName("Delete", true), EditIVOModel.FIND_BY_INTERFACE);
                result.put(this.getFileName("Delete", false), EditIVOModel.FIND_BY);
            }

            return result;
        }

        return null;
    }

    private String getFileName(String prefix, boolean isInterface) {
        return (isInterface ? "I" : "") + prefix + this.definition.getName() + "IVO_v" + this.definition.getVersion();
    }

    /**
     * velocity use
     * @param multiFilter use multifilter
     * @return the interface implementatinos
     */
    public String getInterfaceImplements(boolean multiFilter) {
        return this.getInterfaceImplements();
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
