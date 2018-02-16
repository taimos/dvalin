package de.taimos.dvalin.interconnect.model.maven.imports.ivo;

import de.taimos.dvalin.interconnect.model.ivo.util.IIVOAuditing;
import de.taimos.dvalin.interconnect.model.maven.imports.Imports;
import de.taimos.dvalin.interconnect.model.metamodel.IVODef;

/**
 * @author psigloch
 */
public abstract class BaseIVOImports extends Imports<IVODef> {

    protected void withIVODefinition(IVODef ivoDefenition) {
        this.setIvoPackageName(ivoDefenition.getPkgName());
        if(ivoDefenition.isDeprecated()) {
            this.withToBeReomoved();
        }
        if(ivoDefenition.getAuditing()) {
            this.with(IIVOAuditing.class.getCanonicalName());
        }
        if(ivoDefenition.getParentName() != null) {
            if((ivoDefenition.getParentPkgName() != null) && !ivoDefenition.getParentPkgName().isEmpty() && !ivoDefenition.getPkgName().equals(ivoDefenition.getParentPkgName())) {
                this.with(ivoDefenition.getParentPath(false));
            }
        }
    }
}
