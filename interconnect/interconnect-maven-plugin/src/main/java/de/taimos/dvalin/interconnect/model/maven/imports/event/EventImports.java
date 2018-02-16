package de.taimos.dvalin.interconnect.model.maven.imports.event;

import de.taimos.dvalin.interconnect.model.event.AbstractEvent;
import de.taimos.dvalin.interconnect.model.event.IEventBuilder;
import de.taimos.dvalin.interconnect.model.maven.model.AbstractInterconnectModel;
import de.taimos.dvalin.interconnect.model.metamodel.defs.EventDef;

/**
 * Copyright 2018 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public class EventImports extends BaseEventImports {

    private static final long serialVersionUID = 5867164554321756121L;

    @Override
    public void initDefaults() {
        this.withJsonDeserialize();
        this.withJsonPOJOBuilder();
        this.withNullable();
        this.withNonnull();
        this.with(IEventBuilder.class);
    }

    @Override
    public void initFromDefintion(EventDef ivoDefinition, AbstractInterconnectModel model) {
        super.initFromDefintion(ivoDefinition, model);
        if(!model.hasParentClazz()) {
            this.with(AbstractEvent.class);
        }
    }
}
