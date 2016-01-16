package de.taimos.dvalin.interconnect.model.ivo;

/**
 * IVOs that implement this interface allow paging of the results
 */
public interface IPageable extends IVO {

    /**
     * @return the maximum result size
     **/
    Integer getLimit();

    /**
     * @return the offset of the first result
     **/
    Integer getOffset();

    /**
     * @return the property to sort by
     */
    String getSortBy();

    /**
     * @return the sort direction
     */
    Direction getSortDirection();

    /**
     * @param <T> builder type
     * @return the builder initialized with this
     */
    <T extends IPageableBuilder> T createPageableBuilder();
}
