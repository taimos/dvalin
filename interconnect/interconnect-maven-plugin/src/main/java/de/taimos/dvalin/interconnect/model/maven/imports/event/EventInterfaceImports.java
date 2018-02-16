package de.taimos.dvalin.interconnect.model.maven.imports.event;

import de.taimos.dvalin.interconnect.model.event.IEvent;
import de.taimos.dvalin.interconnect.model.maven.model.AbstractInterconnectModel;
import de.taimos.dvalin.interconnect.model.maven.model.event.AbstractEventModel;
import de.taimos.dvalin.interconnect.model.metamodel.defs.EventDef;

/**
 * @author psigloch
 */
public class EventInterfaceImports extends BaseEventImports {

    private static final long serialVersionUID = -4856513469922204335L;

    @Override
    public void initDefaults() {
        this.withNullable();
        this.withNonnull();
        this.withJsonTypeInfo();
    }

    @Override
    public void initFromDefintion(EventDef ivoDefinition, AbstractInterconnectModel model) {
        super.initFromDefintion(ivoDefinition, model);
        if(model instanceof AbstractEventModel) {
            if(model.hasParentClazz()) {
                this.with(model.getParentInterfacePath());
            } else {
                this.with(IEvent.class);
            }
        }
    }
}
