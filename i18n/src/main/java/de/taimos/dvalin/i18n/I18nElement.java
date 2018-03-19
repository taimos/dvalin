package de.taimos.dvalin.i18n;

import java.util.Map;

/**
 * Copyright 2018 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public class I18nElement {

    private String label;
    private Map<String, String> language;


    /**
     * @return the label
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the language
     */
    public Map<String, String> getLanguage() {
        return this.language;
    }

    /**
     * @param language the language to set
     */
    public void setLanguage(Map<String, String> language) {
        this.language = language;
    }
}
