package de.taimos.dvalin.i18n.yaml;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright 2018 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public class I18nYAMLElements extends HashMap<String, Map<String, String>> {

    private static final long serialVersionUID = -8141172530599069745L;

    @Override
    public Map<String, String> put(String label, Map<String, String> langMap) {
        for(Entry<String, String> langMapElement : langMap.entrySet()) {
            Map<String, String> languageEntry = this.computeIfAbsent(langMapElement.getKey(), k -> new HashMap<>());
            languageEntry.put(label, langMapElement.getValue());
        }
        return null;
    }

}
