package net.sf.yaxv.css.selector;

import net.sf.yaxv.css.CSSContext;
import net.sf.yaxv.css.Selector;

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
