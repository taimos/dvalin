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
    public void initFromDefintion(EventDef ivoDefenition, AbstractInterconnectModel model) {
        this.setIvoPackageName(ivoDefenition.getPkgName());
        if(model.isDeprecated()) {
            this.withToBeRemoved();
        }
        if(model.hasParentClazz()) {
            this.with(model.getParentClazzPath());
        }
    }


}
