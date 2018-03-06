package de.taimos.dvalin.interconnect.model.maven.model.ivo;

import de.taimos.dvalin.interconnect.model.ivo.AbstractIVO;
import de.taimos.dvalin.interconnect.model.maven.imports.ivo.IVOImports;
import de.taimos.dvalin.interconnect.model.metamodel.defs.IVODef;
import org.apache.maven.plugin.logging.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * @author psigloch
 */
public class IVOModel extends AbstractIVOModel {
    private static final String IVO = "ivo/ivo.vm";

    /**
     * @param definition the definition
     * @param logger     the logger
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
        if(this.genereateFile()) {
            result.put(this.getClazzName(), IVOModel.IVO);
        } else if(this.getLogger() != null) {
            this.getLogger().info(this.getClazzName() + " is beyond removal date, only the interface is generated.");
        }
        return result;
    }

    @Override
    public String getParentClazzName() {
        return this.definition.getParentName() == null ? AbstractIVO.class.getSimpleName() : this.getParentClazzName();
    }

    @Override
    public String getParentInterfaceName() {
        return this.definition.getParentName() == null ? AbstractIVO.class.getSimpleName() : this.getParentInterfaceName();
    }

    @Override
    public String getParentClazzPath() {
        return this.definition.getParentName() == null ? AbstractIVO.class.getCanonicalName() : this.definition.getParentPkgName() + "." + this.getParentClazzName();
    }

    @Override
    public boolean hasParentClazz() {
        boolean res = this.definition.getParentName() != null;
        if(res) {
            res = !this.definition.getParentName().trim().isEmpty();
        }
        return res;
    }


    /**
     * velocity use
     *
     * @return provides ivo end addition
     */
    public boolean hasIVOEndAddition() {
        return false;
    }

    /**
     * velocity use
     *
     * @return provides ivo end addition path, relative to resources/ivo
     */
    public String getIVOEndAddition() {
        return "";
    }
}
