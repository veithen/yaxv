package net.sf.yaxv.css;

public class CSSContext {
	private final CSSContext parentContext;
	private final CSSContext siblingContext;

	public CSSContext(CSSContext parentContext, CSSContext siblingContext) {
		this.parentContext = parentContext;
		this.siblingContext = siblingContext;
	}

	public CSSContext getParentContext() { return parentContext; }
	public CSSContext getSiblingContext() { return siblingContext; }
}
