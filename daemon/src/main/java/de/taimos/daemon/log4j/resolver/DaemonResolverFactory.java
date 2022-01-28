package de.taimos.daemon.log4j.resolver;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.layout.template.json.resolver.EventResolverContext;
import org.apache.logging.log4j.layout.template.json.resolver.EventResolverFactory;
import org.apache.logging.log4j.layout.template.json.resolver.TemplateResolver;
import org.apache.logging.log4j.layout.template.json.resolver.TemplateResolverConfig;
import org.apache.logging.log4j.layout.template.json.resolver.TemplateResolverFactory;

/**
 * Copyright 2022 Cinovo AG<br>
 * <br>
 *
 * @author mweise
 */
@Plugin(name = "DaemonResolverFactory", category = TemplateResolverFactory.CATEGORY)
public class DaemonResolverFactory implements EventResolverFactory {

    private static final DaemonResolverFactory INSTANCE = new DaemonResolverFactory();

    private DaemonResolverFactory() {
        // hidden constructor
    }

    @PluginFactory
    public static DaemonResolverFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public String getName() {
        return DaemonResolver.NAME;
    }

    @Override
    public TemplateResolver<LogEvent> create(EventResolverContext context, TemplateResolverConfig config) {
        return new DaemonResolver(config);
    }
}
