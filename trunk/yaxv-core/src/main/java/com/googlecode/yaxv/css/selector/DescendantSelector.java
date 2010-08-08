package com.googlecode.yaxv.css.selector;

import com.googlecode.yaxv.css.CSSContext;
import com.googlecode.yaxv.css.Selector;

public class DescendantSelector extends Selector {
    private final Selector ancestorSelector;
    private final Selector descendantSelector;
    
    public DescendantSelector(Selector ancestorSelector, Selector descendantSelector) {
        this.ancestorSelector = ancestorSelector;
        this.descendantSelector = descendantSelector;
    }

    public boolean selects(CSSContext context) {
        if (descendantSelector.selects(context)) {
            CSSContext ancestorContext = context;
            while ((ancestorContext = ancestorContext.getParentContext()) != null) {
                if (ancestorSelector.selects(ancestorContext)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public String toString() {
        return ancestorSelector + " " + descendantSelector;
    }
}
