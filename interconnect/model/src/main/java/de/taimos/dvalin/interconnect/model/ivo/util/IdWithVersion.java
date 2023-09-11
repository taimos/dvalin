package de.taimos.dvalin.interconnect.model.ivo.util;

import jakarta.annotation.Nonnull;
import java.io.Serializable;

/**
 * Copyright 2022 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public class IdWithVersion implements Serializable {
    private static final long serialVersionUID = -1527470588311279579L;

    private String id;
    private Integer version;

    /**
     *
     */
    public IdWithVersion() {
        //default constructor
    }

    /**
     * @param id the id
     */
    public IdWithVersion(String id) {
        this.id = id;
    }

    /**
     * @param id      the id
     * @param version the version
     */
    public IdWithVersion(String id, Integer version) {
        this.id = id;
        this.version = version;
    }

    /**
     * @return the id
     */
    @Nonnull
    public String getId() {
        return this.id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the version
     */
    public Integer getVersion() {
        return this.version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "id:" + this.id + "-v:" + this.version;
    }
}
