package de.taimos.dvalin.interconnect.model.maven.model.event;

import de.taimos.dvalin.interconnect.model.event.AbstractEvent;
import de.taimos.dvalin.interconnect.model.maven.GenerationContext;
import de.taimos.dvalin.interconnect.model.maven.imports.event.EventImports;
import de.taimos.dvalin.interconnect.model.maven.model.IAdditionalMemberHandler;
import de.taimos.dvalin.interconnect.model.metamodel.defs.EventDef;
import org.apache.maven.plugin.logging.Log;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author psigloch
 */
public class EventModel extends AbstractEventModel {
    private static final String IVO = "event/event.vm";

    /**
     * @param definition               the definition
     * @param logger                   the logger
     * @param additionalMemberHandlers additional member handlers
     */
    public EventModel(EventDef definition, Log logger, IAdditionalMemberHandler... additionalMemberHandlers) {
        super(additionalMemberHandlers);
        this.init(definition, new EventImports(), logger);
    }

    @Override
    public Collection<GenerationContext> getGenerationContexts() {
        Set<GenerationContext> result = new HashSet<>();
        if (this.generateFile()) {
            result.add(new GenerationContext(EventModel.IVO, this.getClazzName(), false));
        } else if (this.getLogger() != null) {
            this.getLogger().info(this.getClazzName() + " is beyond removal date, only the interface is generated.");
        }
        return result;
    }


    @Override
    public String getParentClazzName() {
        return this.definition.getParentName() == null ? AbstractEvent.class.getSimpleName() : this.getParentClazzName();
    }

    @Override
    public String getParentInterfaceName() {
        return this.definition.getParentName() == null ? AbstractEvent.class.getSimpleName() : this.getParentInterfaceName();
    }

    @Override
    public String getParentClazzPath() {
        return this.definition.getParentName() == null ? AbstractEvent.class.getCanonicalName() : this.definition.getParentPkgName() + "." + this.getParentClazzName();
    }

    /**
     * @return the parent builder extends, or null
     */
    @Override
    public String getParentBuilder() {
        return this.hasParentClazz() ? "extends Abstract" + this.getParentClazzName() + "Builder<E>" : "extends AbstractEventBuilder<E>";
    }

    /**
     * @return wheteher the ivo has a parent object or not
     */
    @Override
    public boolean hasParentClazz() {
        return this.definition.getParentName() != null;
    }

    /**
     * velocity use
     *
     * @return provides ivo end addition path, relative to resources/ivo
     */
    public Collection<String> getEventEndAddition() {
        return this.additionalMemberHandlers.stream().map(IAdditionalMemberHandler::getEventTemplateAddition).filter(Objects::nonNull).filter(amh -> !amh.trim().isEmpty()).collect(Collectors.toSet());
    }
}
