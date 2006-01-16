package net.sf.yaxv.css.selector;

public class AttributeSelector extends SimpleSelectorComponent {
	public final static int ANY_VALUE = 1;
	public final static int EXACT = 2;
	public final static int IN_LIST = 3;
	public final static int HYPHEN_MATCH = 4;
	
	private final int type;
	private final String attribute;
	private final String value;
	
	public AttributeSelector(int type, String attribute, String value) {
		this.type = type;
		this.attribute = attribute;
		this.value = value;
	}
}