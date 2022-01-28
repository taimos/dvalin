package de.taimos.daemon.log4j.resolver;

import de.taimos.daemon.DaemonStarter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.layout.template.json.resolver.EventResolver;
import org.apache.logging.log4j.layout.template.json.resolver.TemplateResolverConfig;
import org.apache.logging.log4j.layout.template.json.util.JsonWriter;

/**
 * Copyright 2022 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
public class DaemonResolver implements EventResolver {

    public static final String NAME = "daemon";

    private final String field;

    public DaemonResolver(TemplateResolverConfig config) {
        this.field = config.getString("field");
    }

    @Override
    public void resolve(final LogEvent value, final JsonWriter jsonWriter) {
        jsonWriter.writeString(getValue());
    }

    private String getValue() {
        if (this.field == null) {
            return "";
        }

        switch (this.field) {
            case "name":
                return DaemonStarter.getDaemonName();
            case "host":
                return DaemonStarter.getHostname();
            case "instance":
                return DaemonStarter.getInstanceId();
            case "phase":
                return String.valueOf(DaemonStarter.getCurrentPhase());
            default:
                return "";
        }
    }
}
