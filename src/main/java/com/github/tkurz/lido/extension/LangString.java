package com.github.tkurz.lido.extension;

import java.util.HashMap;
import java.util.Map;

/**
 * ...
 * <p/>
 * Author: Thomas Kurz (tkurz@apache.org)
 */
public class LangString {
    private Map<String,String> langStrings = new HashMap<>();

    public boolean exists(String language) {
        return langStrings.containsKey(language);
    }

    public String getString(String language) {
        return langStrings.get(language);
    }

    public void addString(String language, String value) {
        langStrings.put(language, value);
    }
}
