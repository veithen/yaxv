package com.googlecode.yaxv.css.token;

import com.googlecode.yaxv.css.Token;

public class Hash extends Token {
    private final String name;
    
    public Hash(int line, int column, String name) {
        super(line, column);
        this.name = name;
    }
    
    public String getName() { return name; }
}
