package com.googlecode.yaxv.css;

import java.util.HashSet;
import java.util.Set;
import org.xml.sax.Attributes;

public class CSSContext {
    private final CSSContext parentContext;
    private final CSSContext siblingContext;
    private final int line;
    private final int column;
    private final String name; // TODO: namespace support
    private final Attributes attributes;
    private final Set<String> matchedAttributes = new HashSet<String>();

    public CSSContext(CSSContext parentContext, CSSContext siblingContext, int line, int column, String name, Attributes attributes) {
        this.parentContext = parentContext;
        this.siblingContext = siblingContext;
        this.line = line;
        this.column = column;
        this.name = name;
        this.attributes = attributes;
    }

    public CSSContext getParentContext() { return parentContext; }
    public CSSContext getSiblingContext() { return siblingContext; }
    public int getLineNumber() { return line; }
    public int getColumnNumber() { return column; }
    public String getName() { return name; }
    public Attributes getAttributes() { return attributes; }
    public Set<String> getMatchedAttributes() { return matchedAttributes; }
}
