package com.googlecode.yaxv.css.selector;

import com.googlecode.yaxv.css.CSSContext;
import com.googlecode.yaxv.css.Selector;

public class ChildSelector extends Selector {
    private final Selector parentSelector;
    private final Selector childSelector;
    
    public ChildSelector(Selector parentSelector, Selector childSelector) {
        this.parentSelector = parentSelector;
        this.childSelector = childSelector;
    }

    public boolean selects(CSSContext context) {
        CSSContext parentContext = context.getParentContext();
        return parentContext != null && parentSelector.selects(parentContext) && childSelector.selects(context);
    }
}
