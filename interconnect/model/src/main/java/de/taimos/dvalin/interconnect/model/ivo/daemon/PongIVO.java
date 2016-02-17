package de.taimos.dvalin.interconnect.model.ivo.daemon;

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

import de.taimos.dvalin.interconnect.model.ivo.IVO;
import de.taimos.dvalin.interconnect.model.ivo.IVOBuilder;

public class PongIVO implements IVO {

    /**
     * the version UID for serialization
     */
    private static final long serialVersionUID = 1L;


    /**
     * Builder for the read-only value object
     */
    public static class PongIVOBuilder implements IVOBuilder {

        /**
         * public default constructor for the builder
         */
        public PongIVOBuilder() {
            // nothing to do here, really
        }

        /**
         * public copy constructor for the builder
         *
         * @param ivo the ivo to initialize the builder with
         */
        public PongIVOBuilder(PongIVO ivo) {
            this.initialize(ivo);
        }

        @SuppressWarnings("unused")
        protected void initialize(PongIVO ivo) {
            // copy the fields (shallow copy!)
        }

        @SuppressWarnings("unused")
        protected void copyToVO(PongIVO target) {
            // copy stuff from builder to vo

        }

        /**
         * constructs the read-only value object
         *
         * @return the constructed read-only value object
         */
        @Override
        public PongIVO build() {
            PongIVO result = new PongIVO();
            this.copyToVO(result);
            return result;
        }
    }


    /**
     * Default constructor.
     */
    protected PongIVO() {
        //
    }

    @Override
    public PongIVO clone() {
        try {
            return (PongIVO) super.clone();
        } catch (CloneNotSupportedException e) {
            // cannot happen
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public PongIVOBuilder createBuilder() {
        return new PongIVOBuilder();
    }
}
