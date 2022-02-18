package de.taimos.dvalin.interconnect.model.maven.validation;

import de.taimos.dvalin.interconnect.model.metamodel.memberdef.INamedMemberDef;

/**
 * Copyright 2022 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public class SimpleInvalidFieldNameValidator extends AFieldNameValidator {
    private final String fieldName;

    /**
     * @param fieldName the field name
     */
    public SimpleInvalidFieldNameValidator(String fieldName) {
        if (fieldName != null) {
            this.fieldName = fieldName.trim();
        } else {
            this.fieldName = "";
        }
    }

    @Override
    protected boolean isFieldNameValid(INamedMemberDef child) {
        if (child.getName() == null) {
            return false;
        }
        return !this.fieldName.equalsIgnoreCase(child.getName().trim());
    }
}
