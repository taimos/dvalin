package de.taimos.dvalin.interconnect.model.maven.model;

/**
 * Copyright 2018 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public class ModelTools {

    /**
     * @param buffer string to return with the first char in upper case
     * @return the upper-case-first string
     */
    public String upperCaseFirst(String buffer) {
        if(buffer == null) {
            return "";
        }
        return buffer.length() > 0 ? buffer.substring(0, 1).toUpperCase() + buffer.substring(1) : "";
    }

}
