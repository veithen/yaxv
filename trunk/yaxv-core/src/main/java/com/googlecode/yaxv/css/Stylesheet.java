package com.googlecode.yaxv.css;

import java.util.LinkedList;
import java.util.List;

public class Stylesheet {
    private final Ruleset[] rulesets;
    
    public Stylesheet(Ruleset[] rulesets) {
        this.rulesets = rulesets.clone();
    }
    
    public Ruleset[] getRulesets(CSSContext context) {
        List<Ruleset> result = new LinkedList<Ruleset>();
        for (Ruleset ruleset : rulesets) {
            if (ruleset.appliesTo(context)) {
                result.add(ruleset);
            }
        }
        return result.toArray(new Ruleset[result.size()]);
    }
}
