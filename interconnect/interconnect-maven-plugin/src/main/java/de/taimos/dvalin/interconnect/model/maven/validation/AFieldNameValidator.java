package de.taimos.dvalin.interconnect.model.maven.validation;

import de.taimos.dvalin.interconnect.model.maven.exceptions.DefinitionValidationError;
import de.taimos.dvalin.interconnect.model.metamodel.IGeneratorDefinition;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.INamedMemberDef;

import java.util.stream.Collectors;

/**
 * Copyright 2022 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public abstract class AFieldNameValidator implements IFieldValidator {

    @Override
    public void validate(IGeneratorDefinition ivoDef) throws DefinitionValidationError {
        if (ivoDef == null || ivoDef.getChildren() == null) {
            return;
        }

        for (INamedMemberDef child : ivoDef.getChildren().stream().filter(INamedMemberDef.class::isInstance).map(INamedMemberDef.class::cast).collect(Collectors.toList())) {
            if (!this.isFieldNameValid(child)) {
                throw new DefinitionValidationError("The fieldname " + child.getName() + " of definition " + ivoDef.getName() + " is not allowed.");
            }
        }
    }

    protected abstract boolean isFieldNameValid(INamedMemberDef child);

}
