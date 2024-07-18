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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import de.taimos.dvalin.interconnect.model.ivo.AbstractIVO;
import de.taimos.dvalin.interconnect.model.ivo.IVO;
import de.taimos.dvalin.interconnect.model.ivo.IVOBuilder;

/**
 * the result of an ivoquery
 *
 * @param <E> type of result object
 */
@JsonDeserialize(builder = IVOQueryResultIVO_v1.IVOQueryResultIVO_v1Builder.class)
public class IVOQueryResultIVO_v1<E extends IVO> extends AbstractIVO implements IIVOQueryResultIVO_v1<E> {

    private static final long serialVersionUID = 1L;


    /**
     * Builder for the read-only ivo
     *
     * @param <F> type of reuslt object
     */
    @JsonPOJOBuilder()
    public static class IVOQueryResultIVO_v1Builder<F extends IVO> extends AbstractIVOQueryResultIVO_v1Builder<IVOQueryResultIVO_v1Builder<F>, F> implements IVOBuilder {
        // nothing to do here, really
    }

    /**
     * Abstract Builder for the read-only ivo
     *
     * @param <E> type of builder
     * @param <F> type of result object
     */
    public abstract static class AbstractIVOQueryResultIVO_v1Builder<E extends AbstractIVOQueryResultIVO_v1Builder<?, ?>, F extends IVO> {

        private Long possibleResults;
        private List<F> elements;


        /**
         * the possible results (or null if not applicable)
         *
         * @param ppossibleResults the value to set
         * @return the builder
         **/
        @SuppressWarnings("unchecked")
        public E withPossibleResults(Long ppossibleResults) {
            this.possibleResults = ppossibleResults;
            return (E) this;
        }

        /**
         * the elements retrieved by the request
         *
         * @param pelements the value to set
         * @return the builder
         **/
        @SuppressWarnings("unchecked")
        public E withElements(List<F> pelements) {
            this.elements = pelements;
            return (E) this;
        }

        protected void copyToIVO(IVOQueryResultIVO_v1<F> result) {
            result.possibleResults = this.possibleResults;
            result.elements = this.elements;
        }

        /**
         * @return the entry
         **/
        public IVOQueryResultIVO_v1<F> build() {
            IVOQueryResultIVO_v1<F> result = new IVOQueryResultIVO_v1<>();
            this.copyToIVO(result);
            return result;
        }

    }


    private Long possibleResults = null;
    private List<E> elements = new ArrayList<>();


    protected IVOQueryResultIVO_v1() {
        // hide constructor
    }

    @Override
    public Long getPossibleResults() {
        return this.possibleResults;
    }

    @Override
    public List<E> getElements() {
        return this.elements == null ? null : Collections.unmodifiableList(this.elements);
    }

    @SuppressWarnings("unchecked")
    @Override
    public IVOQueryResultIVO_v1Builder<E> createBuilder() {
        IVOQueryResultIVO_v1Builder<E> builder = new IVOQueryResultIVO_v1Builder<>();
        builder.withElements(this.elements);
        builder.withPossibleResults(this.possibleResults);
        return builder;
    }

    @SuppressWarnings("unchecked")
    @Override
    public IVOQueryResultIVO_v1<E> clone() {
        return (IVOQueryResultIVO_v1<E>) super.clone();
    }


    public static <I extends IVO> IVOQueryResultIVO_v1<I> create(List<I> elements, long count) {
        return new IVOQueryResultIVO_v1Builder<I>().withElements(elements).withPossibleResults(count).build();
    }

}
