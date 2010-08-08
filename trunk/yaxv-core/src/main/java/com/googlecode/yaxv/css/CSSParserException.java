package com.googlecode.yaxv.css;

import com.googlecode.yaxv.Messages;

public class CSSParserException extends Exception {
    private final int line;
    private final int column;
    private final String key;
    
    public CSSParserException(int line, int column, String key) {
        super(Messages.BUNDLE.getString(key));
        this.line = line;
        this.column = column;
        this.key = key;
    }
    
    public int getLineNumber() { return line; }
    public int getColumnNumber() { return column; }
    public String getKey() { return key; }
}
