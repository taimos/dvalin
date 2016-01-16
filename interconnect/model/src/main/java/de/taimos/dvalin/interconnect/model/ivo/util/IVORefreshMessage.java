package de.taimos.dvalin.interconnect.model.ivo.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.taimos.dvalin.interconnect.model.ivo.AbstractIVO;

public class IVORefreshMessage implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Set<String> ivoNames;
    private Set<AbstractIVO> ivos;


    /**
     * @param ivoNames the ivos to be refreshed
     */
    public IVORefreshMessage(Collection<String> ivoNames) {
        this.ivoNames = (ivoNames == null ? new HashSet<String>() : new HashSet<>(ivoNames));
    }

    /**
     * @param ivoNames      the ivos to be refreshed
     * @param ivoCollection collection of ivos
     */
    public IVORefreshMessage(Collection<String> ivoNames, Collection<AbstractIVO> ivoCollection) {
        this.ivoNames = (ivoNames == null ? new HashSet<String>() : new HashSet<>(ivoNames));
        this.ivos = ((ivoCollection == null ? new HashSet<AbstractIVO>() : new HashSet<>(ivoCollection)));
    }

    /**
     * @return the ivoNames
     */
    public Set<String> getIvoNames() {
        return this.ivoNames;
    }

    /**
     * @return the ivos
     */
    public Set<AbstractIVO> getIvos() {
        return this.ivos;
    }
}
