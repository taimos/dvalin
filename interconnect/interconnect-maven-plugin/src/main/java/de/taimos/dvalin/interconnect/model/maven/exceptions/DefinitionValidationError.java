package de.taimos.dvalin.interconnect.model.maven.exceptions;

/**
 * Copyright 2022 <br>
 * <br>
 *
 * @author psigloch
 */
public class DefinitionValidationError extends RuntimeException {
    private static final long serialVersionUID = 4057421652822938877L;

    /**
     * @param message the message
     */
    public DefinitionValidationError(String message) {
        super(message);
    }
}
