package de.taimos.dvalin.interconnect.model.ivo.daemon;

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
