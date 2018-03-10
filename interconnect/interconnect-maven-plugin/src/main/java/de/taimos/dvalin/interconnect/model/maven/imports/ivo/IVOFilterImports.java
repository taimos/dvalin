package de.taimos.dvalin.interconnect.model.maven.imports.ivo;

import de.taimos.dvalin.interconnect.model.ivo.AbstractIVO;
import de.taimos.dvalin.interconnect.model.ivo.Direction;
import de.taimos.dvalin.interconnect.model.ivo.IPageable;
import de.taimos.dvalin.interconnect.model.ivo.IPageableBuilder;
import de.taimos.dvalin.interconnect.model.ivo.IVOBuilder;
import de.taimos.dvalin.interconnect.model.maven.model.AbstractInterconnectModel;
import de.taimos.dvalin.interconnect.model.metamodel.defs.IVODef;

import java.util.Collection;

/**
 * @author psigloch
 */
public class IVOFilterImports extends IVOInterfaceImports {

    private static final long serialVersionUID = -905784989070923147L;

    @Override
    public void initDefaults() {
        super.initDefaults();
        this.withJsonDeserialize();
        this.withJsonPOJOBuilder();
        this.with(Collection.class);
        this.with(IVOBuilder.class);
        this.with(IPageable.class);
        this.with(IPageableBuilder.class);
        this.with(Direction.class);
    }

    @Override
    public void initFromDefintion(IVODef ivoDefinition, AbstractInterconnectModel model) {
        super.initFromDefintion(ivoDefinition, model);

        if((ivoDefinition.getFilterPkgName() != null) && !ivoDefinition.getFilterPkgName().equals(ivoDefinition.getPkgName())) {
            this.with(model.getInterfaceClazzPath());
            this.with(model.getClazzPath());
        }

        if(model.hasParentClazz()) {
            if((ivoDefinition.getFilterPkgName() != null) && ((ivoDefinition.getParentFilterPkgName() == null) || ivoDefinition.getParentFilterPkgName().equals(ivoDefinition.getFilterPkgName()))) {
                this.with(ivoDefinition.getFilterPkgName() + "." + this.getParentIVOFilterName(ivoDefinition) + "." + this.getParentIVONFName(ivoDefinition));
            } else if(ivoDefinition.getFilterPkgName() != null) {
                this.with(ivoDefinition.getParentFilterPkgName() + "." + this.getParentIVOFilterName(ivoDefinition) + "." + this.getParentIVONFName(ivoDefinition));
            }
        }
        if(!model.hasParentClazz()) {
            this.with(AbstractIVO.class);
        }
    }

    /**
     * @return the real filter name
     */
    private String getParentIVOFilterName(IVODef ivoDefinition) {
        return ivoDefinition.getParentName() + "IVOFilter_v" + ivoDefinition.getParentVersion();
    }

    /**
     * @return the real filter name
     */
    private String getParentIVONFName(IVODef ivoDefinition) {
        return ivoDefinition.getParentName() + "IVONF_v" + ivoDefinition.getParentVersion();
    }

}
