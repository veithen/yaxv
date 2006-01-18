package net.sf.yaxv.css.selector;

import net.sf.yaxv.css.CSSContext;

public class IdSelector extends SimpleSelectorComponent {
	private final String id;
	
	public IdSelector(String id) {
		this.id = id;
	}

	public boolean selects(CSSContext context) {
		// TODO: name of id attribute might depend on DTD
		return id.equals(context.getAttributes().getValue("id"));
	}
}
