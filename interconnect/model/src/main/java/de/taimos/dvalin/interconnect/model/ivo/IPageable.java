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
