package de.taimos.dvalin.interconnect.model.maven.validation;

import de.taimos.dvalin.interconnect.model.maven.exceptions.DefinitionValidationError;
import de.taimos.dvalin.interconnect.model.metamodel.IGeneratorDefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2022 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public class DefinitionValidator {

    private static final DefinitionValidator instance = new DefinitionValidator();

    /**
     * @param fieldValidator the field validator to add
     */
    public static void addFieldValidator(IFieldValidator fieldValidator) {
        DefinitionValidator.instance.fieldValidators.add(fieldValidator);
    }

    /**
     * @param ivoDef the definition to validate
     * @throws DefinitionValidationError on error
     */
    public static void validate(IGeneratorDefinition ivoDef) throws DefinitionValidationError {
        DefinitionValidator.instance.fieldValidators.forEach(val -> val.validate(ivoDef));
    }


    private final List<IFieldValidator> fieldValidators = new ArrayList<>();

    private DefinitionValidator() {
        //singleton constructor
    }

}
