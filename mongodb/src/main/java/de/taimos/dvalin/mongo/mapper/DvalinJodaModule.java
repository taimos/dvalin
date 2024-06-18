package de.taimos.dvalin.mongo.mapper;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.joda.time.DateTime;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
public class DvalinJodaModule extends SimpleModule {

    private static final long serialVersionUID = 232046413074427246L;

    public DvalinJodaModule() {
        super(new Version(1, 0, 0, null, "de.taimos.dvalin", "mongodb"));
        this.addSerializer(DateTime.class, new JodaMapping.MongoDateTimeSerializer());
        this.addDeserializer(DateTime.class, new JodaMapping.MongoDateTimeDeserializer());
    }
}
