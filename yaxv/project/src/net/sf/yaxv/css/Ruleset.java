package net.sf.yaxv.css;

public class Ruleset {
	private final Selector[] selectors;
	
	public Ruleset(Selector[] selectors) {
		this.selectors = (Selector[])selectors.clone();
	}
}
