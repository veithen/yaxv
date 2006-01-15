package net.sf.yaxv.css.selector;

import net.sf.yaxv.css.Selector;

public class DescendantSelector extends Selector {
	private final Selector ancestorSelector;
	private final Selector descendantSelector;
	
	public DescendantSelector(Selector ancestorSelector, Selector descendantSelector) {
		this.ancestorSelector = ancestorSelector;
		this.descendantSelector = descendantSelector;
	}
}
