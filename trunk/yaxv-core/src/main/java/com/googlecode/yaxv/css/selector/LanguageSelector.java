package com.googlecode.yaxv.css.selector;

import com.googlecode.yaxv.css.CSSContext;

public class LanguageSelector extends SimpleSelectorComponent {
    private final String lang;
    
    public LanguageSelector(String lang) {
        this.lang = lang;
    }

    public boolean selects(CSSContext context) {
        // TODO: implement this
        return false;
    }
}
