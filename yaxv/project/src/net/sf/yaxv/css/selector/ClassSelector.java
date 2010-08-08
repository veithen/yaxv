package net.sf.yaxv.css.selector;

public class ClassSelector extends AttributeSelector {
    public ClassSelector(String value) {
        super(IN_LIST, "class", value);
    }
}
