package de.taimos.dvalin.interconnect.model.maven.model.event;

import de.taimos.dvalin.interconnect.model.maven.imports.event.BaseEventImports;
import de.taimos.dvalin.interconnect.model.maven.model.AbstractInterconnectModel;
import de.taimos.dvalin.interconnect.model.metamodel.defs.EventDef;

/**
 * @author psigloch
 */
public abstract class AbstractEventModel extends AbstractInterconnectModel<EventDef, BaseEventImports> {

    @Override
    public String getClazzPath() {
        return this.definition.getPackageName() + "." + this.getClazzName();
    }

    @Override
    public String getClazzName() {
        return this.definition.getName() + "Event_v" + this.definition.getVersion();
    }

    @Override
    public String getParentClazzName() {
        return this.definition.getParentName() != null ? this.definition.getParentName() + "Event_v" + this.definition.getParentVersion() : null;
    }

    @Override
    public String getParentClazzPath() {
        return this.definition.getParentPkgName() + "." + this.getParentClazzName();
    }

    /**
     * @return the domain
     */
    public String getDomain() {
        return this.definition.getDomain();
    }
}
