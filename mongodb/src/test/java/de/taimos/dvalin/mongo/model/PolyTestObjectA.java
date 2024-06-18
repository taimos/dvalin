package de.taimos.dvalin.mongo.model;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
public class PolyTestObjectA extends TestObject {

    private static final long serialVersionUID = -660366321316836825L;

    public PolyTestObjectA() {
    }

    private String field;

    /**
     * @return the field
     */
    public String getField() {
        return this.field;
    }

    /**
     * @param field the field to set
     */
    public void setField(String field) {
        this.field = field;
    }
}
