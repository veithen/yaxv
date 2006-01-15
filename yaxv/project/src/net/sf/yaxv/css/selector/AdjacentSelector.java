package net.sf.yaxv.css.selector;

import net.sf.yaxv.css.Selector;

public class AdjacentSelector extends Selector {
	private final Selector firstSelector;
	private final Selector secondSelector;
	
	public AdjacentSelector(Selector firstSelector, Selector secondSelector) {
		this.firstSelector = firstSelector;
		this.secondSelector = secondSelector;
	}
}
