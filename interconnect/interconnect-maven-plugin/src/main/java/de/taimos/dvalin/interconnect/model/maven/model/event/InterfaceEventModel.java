package de.taimos.dvalin.interconnect.model.maven.model.event;

import de.taimos.dvalin.interconnect.model.event.IEvent;
import de.taimos.dvalin.interconnect.model.maven.GenerationContext;
import de.taimos.dvalin.interconnect.model.maven.imports.event.EventInterfaceImports;
import de.taimos.dvalin.interconnect.model.maven.model.IAdditionalMemberHandler;
import de.taimos.dvalin.interconnect.model.metamodel.defs.EventDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.ImplementsDef;
import org.apache.maven.plugin.logging.Log;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author psigloch
 */
public class InterfaceEventModel extends AbstractEventModel {

    private static final String EVENT_INTERFACE = "event/eventInterface.vm";

    /**
     * @param definition the definition
     * @param logger     the logger
     * @param additionalMemberHandlers additional member handlers
     */
    public InterfaceEventModel(EventDef definition, Log logger, IAdditionalMemberHandler... additionalMemberHandlers) {
        super(additionalMemberHandlers);
        this.init(definition, new EventInterfaceImports(), logger);
    }

    @Override
    public Collection<GenerationContext> getGenerationContexts() {
        Set<GenerationContext> result = new HashSet<>();
        result.add(new GenerationContext(InterfaceEventModel.EVENT_INTERFACE, this.getInterfaceClazzName(), true));
        return result;
    }

    @Override
    protected boolean interfaceMode() {
        return true;
    }

    @Override
    protected void beforeChildHandling() {
        super.beforeChildHandling();
        this.addChild(this.getDefaultImplements());
    }

    private ImplementsDef getDefaultImplements() {
        if (this.hasParentClazz()) {
            ImplementsDef def = new ImplementsDef();
            def.setName(this.getParentInterfaceName());
            def.setPkgName(this.definition.getParentPkgName());
            return def;
        }
        return new ImplementsDef(IEvent.class);
    }
}
