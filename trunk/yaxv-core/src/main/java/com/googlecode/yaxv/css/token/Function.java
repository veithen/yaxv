package com.googlecode.yaxv.css.token;

import com.googlecode.yaxv.css.Token;

public class Function extends Token {
    private final String name;
    
    public Function(int line, int column, String name) {
        super(line, column);
        this.name = name;
    }
    
    public String getName() { return name; }
}
