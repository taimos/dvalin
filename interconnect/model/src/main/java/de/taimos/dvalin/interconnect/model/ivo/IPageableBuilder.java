package de.taimos.dvalin.interconnect.model.ivo;

/**
 * IVOBuilders that implement this interface allow paging of the results
 */
public interface IPageableBuilder extends IVOBuilder {

    /**
     * @param limit the maximum number of results
     * @return the builder
     **/
    IPageableBuilder withLimit(Integer limit);

    /**
     * @param offset the offset of the first result
     * @return the builder
     **/
    IPageableBuilder withOffset(Integer offset);

    /**
     * @param sortBy provide this to enable a correct sorted paging of your lists. Use {@link #withSortDirection(Direction)} to provide
     *               information about sort direction
     * @return the builder
     */
    IPageableBuilder withSortBy(String sortBy);

    /**
     * @param direction provide this to enable a correct sorted paging of your lists. Use {@link #withSortBy(String)} to provide information
     *                  about the property to sort by
     * @return the builder
     */
    IPageableBuilder withSortDirection(Direction direction);

}
