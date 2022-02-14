package de.taimos.dvalin.interconnect.model.maven.imports.event;

import de.taimos.dvalin.interconnect.model.event.AbstractEvent;
import de.taimos.dvalin.interconnect.model.event.AbstractEventBuilder;
import de.taimos.dvalin.interconnect.model.event.IEventBuilder;
import de.taimos.dvalin.interconnect.model.maven.imports.Imports;
import de.taimos.dvalin.interconnect.model.maven.model.AbstractInterconnectModel;
import de.taimos.dvalin.interconnect.model.metamodel.defs.EventDef;

/**
 * @author psigloch
 */
public class EventImports extends BaseEventImports {

    private static final long serialVersionUID = 5867164554321756121L;

    @Override
    public void initDefaults() {
        super.initDefaults();
        this.withJsonDeserialize();
        this.withJsonPOJOBuilder();
        this.withNullable();
        this.withNonnull();
        this.add(IEventBuilder.class);
    }

    @Override
    public <K extends AbstractInterconnectModel<EventDef, ? extends Imports<EventDef>>> void initFromDefinition(EventDef ivoDefinition, K model) {
        super.initFromDefinition(ivoDefinition, model);
        if(!model.hasParentClazz()) {
            this.add(AbstractEvent.class);
            this.add(AbstractEventBuilder.class);
        }
    }
}
