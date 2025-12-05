package de.taimos.dvalin.interconnect.core.daemon.handler;

/**
 * Copyright 2025 Cinovo AG<br>
 * <br>
 *
 * @author Philipp Sigloch
 */
public enum MessageHandlerType {
    SINGLE, MULTI;

    /**
     * @param input the string input
     * @return the type, defaults to single
     */
    public static MessageHandlerType from(String input) {
        return ("multi".equals(input)) ? MessageHandlerType.MULTI : MessageHandlerType.SINGLE;
    }
}
