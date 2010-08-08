package com.googlecode.yaxv.css.selector;

import com.googlecode.yaxv.css.CSSContext;
import com.googlecode.yaxv.css.Selector;

public class AdjacentSelector extends Selector {
    private final Selector firstSelector;
    private final Selector secondSelector;
    
    public AdjacentSelector(Selector firstSelector, Selector secondSelector) {
        this.firstSelector = firstSelector;
        this.secondSelector = secondSelector;
    }

    public boolean selects(CSSContext context) {
        CSSContext siblingContext = context.getSiblingContext();
        return siblingContext != null && firstSelector.selects(siblingContext) && secondSelector.selects(context);
    }
}
