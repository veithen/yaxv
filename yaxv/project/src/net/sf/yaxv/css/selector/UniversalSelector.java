package net.sf.yaxv.css.selector;

import net.sf.yaxv.css.CSSContext;

public class UniversalSelector extends BaseSelector {
	public boolean selects(CSSContext context) {
		return true;
	}
}
