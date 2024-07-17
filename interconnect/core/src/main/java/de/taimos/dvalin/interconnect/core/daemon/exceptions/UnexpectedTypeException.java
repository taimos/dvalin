package de.taimos.dvalin.interconnect.core.daemon.exceptions;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
public class UnexpectedTypeException extends Exception {
    private static final long serialVersionUID = -4471574748172345123L;

    /**
     * @param s messaage of the exception
     */
    public UnexpectedTypeException(String s) {
        super(s);
    }
}
