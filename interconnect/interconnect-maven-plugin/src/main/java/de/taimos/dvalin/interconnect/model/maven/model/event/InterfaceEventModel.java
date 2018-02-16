package de.taimos.dvalin.interconnect.model.maven.model.event;

import de.taimos.dvalin.interconnect.model.event.IEvent;
import de.taimos.dvalin.interconnect.model.maven.imports.event.EventInterfaceImports;
import de.taimos.dvalin.interconnect.model.metamodel.defs.EventDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.ImplementsDef;
import org.apache.maven.plugin.logging.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright 2018 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public class InterfaceEventModel extends AbstractEventModel {

    private static final String EVENT_INTERFACE = "event/eventInterface.vm";

    /**
     * @param definition the definition
     * @param logger     the logger
     */
    public InterfaceEventModel(EventDef definition, Log logger) {
        this.init(definition, new EventInterfaceImports(), logger);
    }

    @Override
    public Map<String, String> generateClazzWithTemplates() {
        Map<String, String> result = new HashMap<>();
        result.put(this.getInterfaceClazzName(), InterfaceEventModel.EVENT_INTERFACE);
        return result;
    }

    @Override
    protected boolean interfaceMode() {
        return true;
    }

    protected void beforeChildHandling() {
        super.beforeChildHandling();
        this.definition.getChildren().add(this.getDefaultImplements());
    }

    private ImplementsDef getDefaultImplements() {
        if(this.hasParentClazz()) {
            ImplementsDef def = new ImplementsDef();
            def.setName(this.getParentInterfaceName());
            def.setPkgName(this.definition.getParentPkgName());
            return def;
        }
        return this.getImplementsDef(IEvent.class);
    }
}
