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

import de.taimos.dvalin.interconnect.model.service.DaemonError;
import de.taimos.dvalin.interconnect.model.ivo.IVO;
import de.taimos.dvalin.interconnect.model.ivo.IVOBuilder;

/**
 * Represents an {@link DaemonError} over the wire.
 */

public class DaemonErrorIVO implements IVO {

    /**
     * the version UID for serialization
     */
    private static final long serialVersionUID = 1L;


    /**
     * Builder for the read-only value object
     */
    public static class DaemonErrorIVOBuilder implements IVOBuilder {

        protected int number;
        protected String daemon;
        protected String message;


        /**
         * public default constructor for the builder
         */
        public DaemonErrorIVOBuilder() {
            // nothing to do here, really
        }

        /**
         * public copy constructor for the builder
         *
         * @param ivo the ivo to initialize the builder with
         */
        public DaemonErrorIVOBuilder(DaemonErrorIVO ivo) {
            this.initialize(ivo);
        }

        protected void initialize(DaemonErrorIVO ivo) {
            // copy the fields (shallow copy!)
            this.number = ivo.number;
            this.daemon = ivo.daemon;
            this.message = ivo.message;
        }

        /**
         * @param vnumber Number of the error
         * @return the builder
         */
        public DaemonErrorIVOBuilder number(int vnumber) {
            this.number = vnumber;
            return this;
        }

        /**
         * @param vdaemon Name of the daemon
         * @return the builder
         */
        public DaemonErrorIVOBuilder daemon(String vdaemon) {
            this.daemon = vdaemon;
            return this;
        }

        /**
         * @param vmessage Additional message for the error
         * @return the builder
         */
        public DaemonErrorIVOBuilder message(String vmessage) {
            this.message = vmessage;
            return this;
        }

        protected void copyToVO(DaemonErrorIVO target) {
            target.number = this.number;
            target.daemon = this.daemon;
            target.message = this.message;
        }

        /**
         * constructs the read-only value object
         *
         * @return the constructed read-only value object
         */
        @Override
        public DaemonErrorIVO build() {
            DaemonErrorIVO result = new DaemonErrorIVO();
            this.copyToVO(result);
            return result;
        }

    }


    /**
     * Number of the error
     */
    private int number;
    /**
     * Name of the daemon
     */
    private String daemon;
    /**
     * Additional message for the error
     */
    private String message;


    /**
     * Default constructor.
     */
    protected DaemonErrorIVO() {
        //
    }

    /**
     * @return Number of the error
     */
    public int getNumber() {
        return this.number;
    }

    /**
     * @return Name of the daemon
     */
    public String getDaemon() {
        return this.daemon;
    }

    /**
     * @return Additional message for the error
     */
    public String getMessage() {
        return this.message;
    }

    @Override
    public DaemonErrorIVO clone() {
        try {
            return (DaemonErrorIVO) super.clone();
        } catch (CloneNotSupportedException e) {
            // cannot happen
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public DaemonErrorIVOBuilder createBuilder() {
        DaemonErrorIVOBuilder builder = new DaemonErrorIVOBuilder();
        builder.daemon(this.daemon);
        builder.message(this.message);
        builder.number(this.number);
        return builder;
    }

}
