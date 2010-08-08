package com.googlecode.yaxv.css.selector;

import com.googlecode.yaxv.css.CSSContext;

public class UniversalSelector extends BaseSelector {
    public boolean selects(CSSContext context) {
        return true;
    }
}
