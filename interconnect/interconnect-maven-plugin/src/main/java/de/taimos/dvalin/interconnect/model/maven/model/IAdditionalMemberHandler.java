package de.taimos.dvalin.interconnect.model.maven.model;

import de.taimos.dvalin.interconnect.model.metamodel.IGeneratorDefinition;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.ContentDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.ContentType;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.ImplementsDef;

import java.util.Collection;

/**
 * Copyright 2022 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public interface IAdditionalMemberHandler {

    /**
     * @return the handlers identifier
     */
    String getIdentifier();

    /**
     * @return true, if handler contains additional member definitions
     */
    boolean isMemberDef();

    /**
     * @param type the content type to identify with
     * @return true if applicable for content type
     */
    boolean isContentType(ContentType type);

    /**
     * @param content the content definition
     * @return additional imports needed for the member
     */
    Collection<String> getMemberContentImports(ContentDef content);

    /**
     * @param member the member to be
     * @return additional imports needed for the member
     */
    Collection<String> getMemberImports(Object member);

    /**
     * @return true, if global additions should be made
     */
    boolean hasGlobalAdditions();

    /**
     * @param definition the definition
     * @return the imports which should be made globally
     */
    Collection<String> getGlobalImports(IGeneratorDefinition definition);

    /**
     * @param definition the definition
     * @return the additional interfaces to be added
     */
    Collection<ImplementsDef> getGlobalImplements(IGeneratorDefinition definition);

    /**
     * @param definition the definition
     * @return the additional fields to be added
     */
    Collection<Object> getGlobalChildren(IGeneratorDefinition definition);

    /**
     * @return the name of the template addition used in ivos, might be null
     */
    String getIVOTemplateAddition();

    /**
     * @return the name of the template addition used in events, might be null
     */
    String getEventTemplateAddition();

    /**
     * prepare contexts, called before childHandling
     *
     * @param definition the definition
     */
    void prepare(IGeneratorDefinition definition);
}
