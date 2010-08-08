package net.sf.yaxv.css;

// TODO: Ruleset or RuleSet ??
public class Ruleset {
    private final Selector[] selectors;
    
    public Ruleset(Selector[] selectors) {
        this.selectors = (Selector[])selectors.clone();
    }
    
    public boolean appliesTo(CSSContext context) {
        for (int i=0; i<selectors.length; i++) {
            if (selectors[i].selects(context)) {
                return true;
            }
        }
        return false;
    }
    
    public String toString() {
        StringBuffer buff = new StringBuffer();
        for (int i=0; i<selectors.length; i++) {
            if (i>0) {
                buff.append(", ");
            }
            buff.append(selectors[i].toString());
        }
        buff.append(" { ... }");
        return buff.toString();
    }
}
