package de.taimos.dvalin.interconnect.model.ivo;

/**
 * Implemented by IVOs that are user specific, i.e. contain a coreId/userId
 */
public interface IUserSpecificBuilder extends IVOBuilder {

    /**
     * @param userId the core² id
     * @return the builder
     */
    IUserSpecificBuilder withUserId(Long userId);

}
