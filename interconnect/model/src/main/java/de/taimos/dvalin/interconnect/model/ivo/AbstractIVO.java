package de.taimos.dvalin.interconnect.model.ivo;


/**
 * Convenience base class for IVOs
 */
public abstract class AbstractIVO implements IVO {

    private static final long serialVersionUID = 1L;


    @Override
    public IVO clone() {
        try {
            return (IVO) super.clone();
        } catch (CloneNotSupportedException e) {
            // cannot happen
            throw new RuntimeException("Cloning of IVO failed", e);
        }
    }


}
