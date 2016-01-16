package de.taimos.dvalin.interconnect.model.ivo;

/**
 * Defines sort direction
 */
public enum Direction {

    /**
     * Ascending sort direction
     **/
    ASC(1),

    /**
     * Descending sort direction
     **/
    DESC(-1);

    private final int direction;


    private Direction(int direction) {
        this.direction = direction;
    }

    /**
     * @return the sort direction
     **/
    public int direction() {
        return this.direction;
    }
}
