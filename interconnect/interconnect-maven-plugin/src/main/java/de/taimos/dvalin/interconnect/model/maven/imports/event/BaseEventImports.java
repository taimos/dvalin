package de.taimos.dvalin.interconnect.model.maven.imports.event;

import de.taimos.dvalin.interconnect.model.maven.imports.Imports;
import de.taimos.dvalin.interconnect.model.maven.model.AbstractInterconnectModel;
import de.taimos.dvalin.interconnect.model.metamodel.defs.EventDef;

/**
 * @author psigloch
 */
public abstract class BaseEventImports extends Imports<EventDef> {

    private static final long serialVersionUID = -2807251568965489734L;

    @Override
    public void initDefaults() {
        this.withJsonIgnoreProperties();
    }

    @Override
    public <K extends AbstractInterconnectModel<EventDef, ? extends Imports<EventDef>>> void initFromDefinition(EventDef ivoDefinition, K model) {
        this.setIvoPackageName(ivoDefinition.getPkgName());
        if (model.isDeprecated()) {
            this.withToBeRemoved();
        }
        if (model.hasParentClazz()) {
            this.add(model.getParentClazzPath());
        }
    }


}
