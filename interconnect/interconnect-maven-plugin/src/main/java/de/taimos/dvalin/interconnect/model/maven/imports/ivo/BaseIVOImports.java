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
    public void initDefaults() {
        this.withJsonIgnoreProperties();
    }

    @Override
    public <K extends AbstractInterconnectModel<IVODef, ? extends Imports<IVODef>>> void initFromDefinition(IVODef ivoDefinition, K model) {
        this.setIvoPackageName(ivoDefinition.getPkgName());
        if (model.isDeprecated()) {
            this.withToBeRemoved();
        }
        if (Boolean.TRUE.equals(ivoDefinition.getAuditing())) {
            this.add(IIVOAuditing.class);
            this.withDateTime();
        }
        if (model.hasParentClazz()) {
            this.add(model.getParentClazzPath());
        }
    }
}
