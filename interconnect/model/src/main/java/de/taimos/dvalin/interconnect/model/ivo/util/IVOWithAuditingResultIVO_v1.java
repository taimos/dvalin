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
import de.taimos.dvalin.interconnect.model.ivo.IVOBuilder;

/**
 * an ivo with its auditing
 *
 * @param <E> type of result object
 */
@JsonDeserialize(builder = IVOWithAuditingResultIVO_v1.IVOWithAuditingResultIVO_v1Builder.class)
public class IVOWithAuditingResultIVO_v1<E extends IIVOAuditing> extends AbstractIVO implements IIVOWithAuditingResultIVO_v1<E> {

    private static final long serialVersionUID = 1L;


    /**
     * Builder for the read-only ivo
     *
     * @param <F> type of reuslt object
     */
    @JsonPOJOBuilder()
    public static class IVOWithAuditingResultIVO_v1Builder<F extends IIVOAuditing> extends AbstractIVOWithAuditingResultIVO_v1Builder<IVOWithAuditingResultIVO_v1Builder<F>, F> implements IVOBuilder {

        // nothing to do here, really

    }

    /**
     * Abstract Builder for the read-only ivo
     *
     * @param <E> type of builder
     * @param <F> type of result object
     */
    public abstract static class AbstractIVOWithAuditingResultIVO_v1Builder<E extends AbstractIVOWithAuditingResultIVO_v1Builder<?, ?>, F extends IIVOAuditing> {

        private F element;
        private List<F> auditingElements;


        /**
         * the possible results (or null if not applicable)
         *
         * @param pelement the value to set
         * @return the builder
         **/
        @SuppressWarnings("unchecked")
        public E withElement(F pelement) {
            this.element = pelement;
            return (E) this;
        }

        /**
         * the elements retrieved by the request
         *
         * @param pauditingElements the value to set
         * @return the builder
         **/
        @SuppressWarnings("unchecked")
        public E withAuditingElements(List<F> pauditingElements) {
            this.auditingElements = pauditingElements;
            return (E) this;
        }

        protected void copyToIVO(IVOWithAuditingResultIVO_v1<F> result) {
            result.element = this.element;
            result.auditingElements = this.auditingElements;
        }

        /**
         * @return the entry
         **/
        public IVOWithAuditingResultIVO_v1<F> build() {
            IVOWithAuditingResultIVO_v1<F> result = new IVOWithAuditingResultIVO_v1<>();
            this.copyToIVO(result);
            return result;
        }

    }


    private E element = null;
    private List<E> auditingElements = new ArrayList<>();


    protected IVOWithAuditingResultIVO_v1() {
        // hide constructor
    }

    @Override
    public E getElement() {
        return this.element;
    }

    @Override
    public List<E> getAuditingElements() {
        return this.auditingElements == null ? null : Collections.unmodifiableList(this.auditingElements);
    }

    @SuppressWarnings("unchecked")
    @Override
    public IVOWithAuditingResultIVO_v1Builder<E> createBuilder() {
        IVOWithAuditingResultIVO_v1Builder<E> builder = new IVOWithAuditingResultIVO_v1Builder<>();
        builder.withAuditingElements(this.auditingElements);
        builder.withElement(this.element);
        return builder;
    }

    @SuppressWarnings("unchecked")
    @Override
    public IVOWithAuditingResultIVO_v1<E> clone() {
        return (IVOWithAuditingResultIVO_v1<E>) super.clone();
    }

}
