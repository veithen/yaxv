package net.sf.yaxv.css.selector;

public class PseudoClassSelector extends SimpleSelectorComponent {
	public final static int FIRST_CHILD = 1;
	public final static int LINK = 2;
	public final static int VISITED = 3;
	public final static int ACTIVE = 4;
	public final static int HOVER = 5;
	public final static int FOCUS = 6;
	
	private final int type;
	
	public PseudoClassSelector(int type) {
		this.type = type;
	}
}
