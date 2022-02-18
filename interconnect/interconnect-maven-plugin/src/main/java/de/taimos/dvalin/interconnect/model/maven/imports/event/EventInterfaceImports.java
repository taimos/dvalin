package de.taimos.dvalin.interconnect.model.maven.imports.event;

import de.taimos.dvalin.interconnect.model.event.EventDomain;
import de.taimos.dvalin.interconnect.model.event.IEvent;
import de.taimos.dvalin.interconnect.model.maven.imports.Imports;
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
        super.initDefaults();
        this.withNullable();
        this.withNonnull();
        this.withJsonTypeInfo();
        this.withJsonIgnoreProperties();
        this.add(EventDomain.class);
    }

    @Override
    public <K extends AbstractInterconnectModel<EventDef, ? extends Imports<EventDef>>> void initFromDefinition(EventDef ivoDefinition, K model) {
        super.initFromDefinition(ivoDefinition, model);
        if (model instanceof AbstractEventModel) {
            if (model.hasParentClazz()) {
                this.add(model.getParentInterfacePath());
            } else {
                this.add(IEvent.class);
            }
        }
    }
}
