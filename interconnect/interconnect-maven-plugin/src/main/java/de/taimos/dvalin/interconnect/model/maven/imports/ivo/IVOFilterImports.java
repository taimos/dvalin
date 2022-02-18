package de.taimos.dvalin.interconnect.model.maven.imports.ivo;

import de.taimos.dvalin.interconnect.model.ivo.Direction;
import de.taimos.dvalin.interconnect.model.ivo.IPageable;
import de.taimos.dvalin.interconnect.model.ivo.IPageableBuilder;
import de.taimos.dvalin.interconnect.model.ivo.IVOBuilder;
import de.taimos.dvalin.interconnect.model.maven.imports.Imports;
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
        this.add(Collection.class);
        this.add(IVOBuilder.class);
        this.add(IPageable.class);
        this.add(IPageableBuilder.class);
        this.add(Direction.class);
    }

    @Override
    public <K extends AbstractInterconnectModel<IVODef, ? extends Imports<IVODef>>> void initFromDefinition(IVODef ivoDefinition, K model) {
        super.initFromDefinition(ivoDefinition, model);

        if((ivoDefinition.getFilterPkgName() != null) && !ivoDefinition.getFilterPkgName().equals(ivoDefinition.getPkgName())) {
            this.add(model.getInterfaceClazzPath());
            this.add(model.getClazzPath());
        }

        if(model.hasParentClazz()) {
            if((ivoDefinition.getFilterPkgName() != null) && ((ivoDefinition.getParentFilterPkgName() == null) || ivoDefinition.getParentFilterPkgName().equals(ivoDefinition.getFilterPkgName()))) {
                this.add(ivoDefinition.getFilterPkgName() + "." + this.getParentIVOFilterName(ivoDefinition) + "." + this.getParentIVONFName(ivoDefinition));
            } else if(ivoDefinition.getFilterPkgName() != null) {
                this.add(ivoDefinition.getParentFilterPkgName() + "." + this.getParentIVOFilterName(ivoDefinition) + "." + this.getParentIVONFName(ivoDefinition));
            }
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
