package de.taimos.dvalin.interconnect.model.maven.imports.ivo;

import de.taimos.dvalin.interconnect.model.ivo.Direction;
import de.taimos.dvalin.interconnect.model.ivo.IPageable;
import de.taimos.dvalin.interconnect.model.ivo.IPageableBuilder;
import de.taimos.dvalin.interconnect.model.ivo.IVOBuilder;
import de.taimos.dvalin.interconnect.model.metamodel.IVODef;

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
    }

    public void initFromDefintion(IVODef ivoDefinition) {
        super.initFromDefintion(ivoDefinition);

        if((ivoDefinition.getFilterPkgName() != null) && !ivoDefinition.getFilterPkgName().equals(ivoDefinition.getPkgName())) {
            this.with(ivoDefinition.getIVOPath(true));
            this.with(ivoDefinition.getIVOPath(false));
        }

        if(ivoDefinition.getParentName() != null) {
            if((ivoDefinition.getFilterPkgName() != null) && ((ivoDefinition.getParentFilterPkgName() == null) || ivoDefinition.getParentFilterPkgName().equals(ivoDefinition.getFilterPkgName()))) {
                this.with(ivoDefinition.getFilterPkgName() + "." + this.getParentIVOFilterName(ivoDefinition) + "." + this.getParentIVONFName(ivoDefinition));
            } else if(ivoDefinition.getFilterPkgName() != null) {
                this.with(ivoDefinition.getParentFilterPkgName() + "." + this.getParentIVOFilterName(ivoDefinition) + "." + this.getParentIVONFName(ivoDefinition));
            }
        }

        this.with(IPageable.class);
        this.with(IPageableBuilder.class);
        this.with(Direction.class);
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
