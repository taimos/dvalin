package de.taimos.dvalin.interconnect.model.maven.model;

/**
 * @author psigloch
 */
public class ModelTools {

    /**
     * @param buffer string to return add the first char in upper case
     * @return the upper-case-first string
     */
    public String upperCaseFirst(String buffer) {
        if(buffer == null) {
            return "";
        }
        return !buffer.isEmpty() ? buffer.substring(0, 1).toUpperCase() + buffer.substring(1) : "";
    }

}
