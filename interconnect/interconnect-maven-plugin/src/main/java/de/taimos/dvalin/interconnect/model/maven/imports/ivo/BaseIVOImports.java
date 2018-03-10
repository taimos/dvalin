package de.taimos.dvalin.interconnect.model.maven.imports.ivo;

import de.taimos.dvalin.interconnect.model.ivo.util.IIVOAuditing;
import de.taimos.dvalin.interconnect.model.maven.imports.Imports;
import de.taimos.dvalin.interconnect.model.maven.model.AbstractInterconnectModel;
import de.taimos.dvalin.interconnect.model.metamodel.defs.IVODef;

/**
 * @author psigloch
 */
public abstract class BaseIVOImports extends Imports<IVODef> {

    private static final long serialVersionUID = -2807251568965489734L;

    @Override
    public void initFromDefintion(IVODef ivoDefenition, AbstractInterconnectModel model) {
        this.setIvoPackageName(ivoDefenition.getPkgName());
        if(model.isDeprecated()) {
            this.withToBeRemoved();
        }
        if(ivoDefenition.getAuditing()) {
            this.with(IIVOAuditing.class);
        }
        if(model.hasParentClazz()) {
            this.with(model.getParentClazzPath());
        }
    }
}
