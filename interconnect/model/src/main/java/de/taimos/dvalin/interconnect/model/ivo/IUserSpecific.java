package de.taimos.dvalin.interconnect.model.ivo;

/**
 * Implemented by IVOs that are user specific, i.e. contain a coreId/userId
 */
public interface IUserSpecific extends IVO {

    /**
     * @return the core² user id
     **/
    Long getUserId();

    /**
     * @param <T> builder type
     * @return the builder initialized with this
     */
    <T extends IUserSpecificBuilder> T createUserSpecificBuilder();
}
