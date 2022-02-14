package de.taimos.dvalin.interconnect.model.maven.validation;

import de.taimos.dvalin.interconnect.model.maven.exceptions.DefinitionValidationError;
import de.taimos.dvalin.interconnect.model.metamodel.IGeneratorDefinition;

/**
 * Copyright 2022 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public interface IFieldValidator {
    /**
     * @param ivoDef the
     * @throws DefinitionValidationError on error
     */
    void validate(IGeneratorDefinition ivoDef) throws DefinitionValidationError;
}
