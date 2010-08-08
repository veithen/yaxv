package com.googlecode.yaxv.css.token;

import com.googlecode.yaxv.css.Token;

public class Percentage extends Token {
    private final String value;
    
    public Percentage(int line, int column, String value) {
        super(line, column);
        this.value = value;
    }
    
    public String getValue() { return value; }
}
