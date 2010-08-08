package net.sf.yaxv.css.selector;

import net.sf.yaxv.css.CSSContext;

public class TypeSelector extends BaseSelector {
    private final String name;
    
    public TypeSelector(String name) {
        this.name = name;
    }

    public boolean selects(CSSContext context) {
        // TODO: namespaces???
        return name.equals(context.getName());
    }
    
    public String toString() {
        return name;
    }
}
