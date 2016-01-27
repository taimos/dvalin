package de.taimos.dvalin.interconnect.model.ivo.daemon;

import de.taimos.dvalin.interconnect.model.ivo.IVO;
import de.taimos.dvalin.interconnect.model.ivo.IVOBuilder;

public class PingIVO implements IVO {

    /**
     * the version UID for serialization
     */
    private static final long serialVersionUID = 1L;


    /**
     * Builder for the read-only value object
     */
    public static class PingIVOBuilder implements IVOBuilder {

        /**
         * public default constructor for the builder
         */
        public PingIVOBuilder() {
            // nothing to do here, really
        }

        /**
         * public copy constructor for the builder
         *
         * @param ivo the ivo to initialize the builder with
         */
        public PingIVOBuilder(PingIVO ivo) {
            this.initialize(ivo);
        }

        @SuppressWarnings("unused")
        protected void initialize(PingIVO ivo) {
            // copy the fields (shallow copy!)
        }

        @SuppressWarnings("unused")
        protected void copyToVO(PingIVO target) {
            // copy stuff from builder to vo

        }

        /**
         * constructs the read-only value object
         *
         * @return the constructed read-only value object
         */
        @Override
        public PingIVO build() {
            PingIVO result = new PingIVO();
            this.copyToVO(result);
            return result;
        }
    }


    /**
     * Default constructor.
     */
    protected PingIVO() {
        //
    }

    @Override
    public PingIVO clone() {
        try {
            return (PingIVO) super.clone();
        } catch (CloneNotSupportedException e) {
            // cannot happen
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public PingIVOBuilder createBuilder() {
        return new PingIVOBuilder();
    }

}
