package de.taimos.dvalin.interconnect.model.maven.imports.ivo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.taimos.dvalin.interconnect.model.ivo.IIdentity;
import de.taimos.dvalin.interconnect.model.metamodel.IVODef;

/**
 * @author psigloch
 */
public class IVOInterfaceImports extends BaseIVOImports {

    private static final long serialVersionUID = -4856513469922204335L;

    @Override
    public void initDefaults() {
        this.withNullable();
        this.withNunnull();
        this.withJsonTypeInfo();
    }

    @Override
    public void initFromDefintion(IVODef ivoDefinition) {
        this.withIVODefinition(ivoDefinition);

        if(Boolean.TRUE.equals(ivoDefinition.getIdentity())) {
            this.with(JsonIgnore.class);
        }

        if(ivoDefinition.getParentName() != null) {
            if((ivoDefinition.getParentPkgName() != null) && !ivoDefinition.getParentPkgName().isEmpty() && !ivoDefinition.getPkgName().equals(ivoDefinition.getParentPkgName())) {
                this.with(ivoDefinition.getParentPath(true));
            }
        }

        if(ivoDefinition.getIdentity() != null && ivoDefinition.getIdentity()) {
            if(ivoDefinition.getParentName() == null) {
                this.with(IIdentity.class);
            }
        }
    }
}
