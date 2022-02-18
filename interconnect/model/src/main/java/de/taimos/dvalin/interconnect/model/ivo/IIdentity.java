package de.taimos.dvalin.interconnect.model.ivo;

/**
 * @author psigloch
 */
public interface IIdentity {
    /**
     * property constant for the id
     **/
    String PROP_ID = "id";

    /**
     * the id
     *
     * @return the value for id
     **/
    String getId();

    /**
     * the id as long
     *
     * @return the value for id
     **/
    long getIdAsLong();
}
