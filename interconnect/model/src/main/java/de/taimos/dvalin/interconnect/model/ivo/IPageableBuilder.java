package de.taimos.dvalin.interconnect.model.ivo;

/*
 * #%L
 * Dvalin interconnect transfer data model
 * %%
 * Copyright (C) 2016 Taimos GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

/**
 * IVOBuilders that implement this interface allow paging of the results
 */
public interface IPageableBuilder {

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

    /**
     * @param <R> the result
     * @return the initialized IPageable
     */
    <R extends IPageable> R build();

}
