package de.taimos.dvalin.interconnect.core.daemon.handler;

import de.taimos.dvalin.interconnect.model.InterconnectObject;
import de.taimos.dvalin.interconnect.model.ivo.IPageable;

/**
 * Copyright 2025 Cinovo AG<br>
 * <br>
 *
 * @author Philipp Sigloch
 */
public interface DefaultMessageLogAppender {
    /**
     * @param ivo the ivo
     * @return the string to append to log
     */
    static String pageAppender(InterconnectObject ivo) {
        if (ivo instanceof IPageable) {
            return "at Page " + DefaultMessageLogAppender.asBoundString(((IPageable) ivo).getOffset()) + ";" + DefaultMessageLogAppender.asBoundString(((IPageable) ivo).getLimit());
        }
        return null;
    }

    /**
     * @param value the integer value
     * @return each as string: "0" if <= 0; "MAX" if Integer.MAX; value otherwise
     */
    static String asBoundString(Integer value) {
        if ((value == null || (value < 0))) {
            return "0";
        }
        if ((value >= Integer.MAX_VALUE)) {
            return "MAX";
        }
        return String.valueOf(value);
    }

}
