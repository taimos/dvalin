package de.taimos.dvalin.interconnect.model.maven.model.event;

import de.taimos.dvalin.interconnect.model.event.AbstractEvent;
import de.taimos.dvalin.interconnect.model.maven.imports.event.EventImports;
import de.taimos.dvalin.interconnect.model.metamodel.defs.EventDef;
import org.apache.maven.plugin.logging.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * @author psigloch
 */
public class EventModel extends AbstractEventModel {
    private static final String IVO = "event/event.vm";

    /**
     * @param definition the definition
     * @param logger     the logger
     */
    public EventModel(EventDef definition, Log logger) {
        this.init(definition, new EventImports(), logger);
    }

    @Override
    public Map<String, String> generateClazzWithTemplates() {
        Map<String, String> result = new HashMap<>();
        if(this.genereateFile()) {
            result.put(this.getClazzName(), EventModel.IVO);
        } else if(this.getLogger() != null) {
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
     * @return wheteher the ivo has a parent object or not
     */
    public boolean hasParentClazz() {
        return this.definition.getParentName() != null;
    }

    /**
     * velocity use
     *
     * @return provides ivo end addition
     */
    public boolean hasEventEndAddition() {
        return false;
    }

    /**
     * velocity use
     *
     * @return provides ivo end addition path, relative to resources/ivo
     */
    public String getEventEndAddition() {
        return "";
    }
}
