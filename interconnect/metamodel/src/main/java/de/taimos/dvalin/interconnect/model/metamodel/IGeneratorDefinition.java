package de.taimos.dvalin.interconnect.model.metamodel;

import java.util.List;

/**
 * Copyright 2018 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public interface IGeneratorDefinition {

    /**
     * @return the children
     */
    List<Object> getChildren();

    /**
     * @param children the children to set
     */
    void setChildren(List<Object> children);

    /**
     * @return the package name to generate to;
     */
    String getPackageName();
}
