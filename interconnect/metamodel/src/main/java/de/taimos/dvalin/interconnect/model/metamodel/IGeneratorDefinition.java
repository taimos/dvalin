package de.taimos.dvalin.interconnect.model.metamodel;

import java.util.List;

/**
 * @author psigloch
 */
public interface IGeneratorDefinition {

    /**
     * @return the definition name
     */
    String getName();

    /**
     * @return the children
     */
    List<Object> getChildren();

    /**
     * @return the package name to generate to;
     */
    String getPackageName();

    /**
     * @return the author
     */
    String getAuthor();

    /**
     * @return the comment
     */
    String getComment();

    /**
     * @return the removedate as string
     */
    String getRemovalDate();

    /**
     * @return the serial version from the defintion
     */
    Integer getVersion();
}
