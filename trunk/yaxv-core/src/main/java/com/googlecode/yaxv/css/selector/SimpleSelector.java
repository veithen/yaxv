package com.googlecode.yaxv.css.selector;

import com.googlecode.yaxv.css.CSSContext;
import com.googlecode.yaxv.css.Selector;

public class SimpleSelector extends Selector {
    private final BaseSelector baseSelector;
    private final SimpleSelectorComponent[] components;
    
    public SimpleSelector(BaseSelector baseSelector, SimpleSelectorComponent[] components) {
        this.baseSelector = baseSelector;
        this.components = components.clone();
    }

    public boolean selects(CSSContext context) {
        if (!baseSelector.selects(context)) {
            return false;
        }
        for (SimpleSelectorComponent component : components) {
            if (!component.selects(context)) {
                return false;
            }
        }
        return true;
    }
    
    public String toString() {
        StringBuffer buff = new StringBuffer();
        buff.append(baseSelector);
        for (int i=0; i<components.length; i++) {
            buff.append(components[i]);
        }
        return buff.toString();
    }
}
