package de.taimos.dvalin.interconnect.model.ivo;


import de.taimos.dvalin.interconnect.model.InterconnectObject;

/**
 * Interconnect value object marker interface. Common base class for all interconnect value objects.
 */
public interface IVO extends InterconnectObject {

    /**
     * @return a clone
     */
    @Override
    IVO clone();

    /**
     * @param <T> builder type
     * @return the builder initialized with this
     */
    <T extends IVOBuilder> T createBuilder();

}
