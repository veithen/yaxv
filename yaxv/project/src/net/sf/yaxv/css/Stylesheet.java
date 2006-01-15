package net.sf.yaxv.css;

public class Stylesheet {
	private final Ruleset[] rulesets;
	
	public Stylesheet(Ruleset[] rulesets) {
		this.rulesets = (Ruleset[])rulesets.clone();
	}
}
