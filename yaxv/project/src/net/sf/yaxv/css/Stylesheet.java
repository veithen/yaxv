package net.sf.yaxv.css;

import java.util.LinkedList;
import java.util.List;

public class Stylesheet {
	private final Ruleset[] rulesets;
	
	public Stylesheet(Ruleset[] rulesets) {
		this.rulesets = (Ruleset[])rulesets.clone();
	}
	
	public Ruleset[] getRulesets(CSSContext context) {
		List result = new LinkedList();
		for (int i=0; i<rulesets.length; i++) {
			if (rulesets[i].appliesTo(context)) {
				result.add(rulesets[i]);
			}
		}
		return (Ruleset[])result.toArray(new Ruleset[result.size()]);
	}
}
