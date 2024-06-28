package de.taimos.dvalin.interconnect.model.ivo;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
public class PageableSort {

    private final String field;
    private final Direction direction;

    /**
     * @param field     to sort by
     * @param direction to sort by
     */
    public PageableSort(String field, Direction direction) {
        this.field = field;
        this.direction = direction;
    }

    /**
     * @return the field
     */
    public String getField() {
        return this.field;
    }

    /**
     * @return the direction
     */
    public Direction getDirection() {
        return this.direction;
    }
}
