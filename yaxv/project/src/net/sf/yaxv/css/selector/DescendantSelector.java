package net.sf.yaxv.css.selector;

import net.sf.yaxv.css.CSSContext;
import net.sf.yaxv.css.Selector;

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
