package de.taimos.dvalin.mongo;

import com.mongodb.client.model.Filters;
import de.taimos.dvalin.mongo.model.AuditedTestObject;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
public class TestAuditedDAO extends AbstractAuditedMongoDAO<AuditedTestObject> {
    public AuditedTestObject findByName(String name) {
        return this.findFirstByQuery(Filters.eq("name", name), null);
    }
}
