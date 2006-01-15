package net.sf.yaxv.css.selector;

import net.sf.yaxv.css.Selector;

public class SimpleSelector extends Selector {
	private final BaseSelector baseSelector;
	private final SimpleSelectorComponent[] components;
	
	public SimpleSelector(BaseSelector baseSelector, SimpleSelectorComponent[] components) {
		this.baseSelector = baseSelector;
		this.components = (SimpleSelectorComponent[])components.clone();
	}
	
}
