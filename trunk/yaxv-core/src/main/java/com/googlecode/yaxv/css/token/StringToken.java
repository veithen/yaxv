package com.googlecode.yaxv.css.token;

import com.googlecode.yaxv.css.Token;

public class StringToken extends Token {
    private final String content;
    
    public StringToken(int line, int column, String content) {
        super(line, column);
        this.content = content;
    }
    
    public String getContent() { return content; }
}
