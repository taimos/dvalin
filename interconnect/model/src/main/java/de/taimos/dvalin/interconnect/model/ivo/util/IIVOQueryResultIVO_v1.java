package de.taimos.dvalin.interconnect.model.ivo.util;

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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import de.taimos.dvalin.interconnect.model.ivo.IVO;

/**
 * the result of an ivoquery
 *
 * @param <E> type of value object
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public interface IIVOQueryResultIVO_v1<E extends IVO> {

    /**
     * property constant for possibleResults property comment: the possible results (or null if not applicable)
     */
    public static final String PROP_POSSIBLERESULTS = "possibleResults";
    /**
     * property constant for elements property comment: the elements retrieved by the request
     */
    public static final String PROP_ELEMENTS = "elements";


    /**
     * the possible results (or null if not applicable)
     *
     * @return the value for possibleResults
     **/
    Long getPossibleResults();

    /**
     * the elements retrieved by the request
     *
     * @return the value for elements
     **/
    List<E> getElements();

    /**
     * @return a clone
     */
    IIVOQueryResultIVO_v1<E> clone();

}
