package net.sf.yaxv.css.token;

import net.sf.yaxv.css.Token;

public class NumberToken extends Token {
    private final String value;
    
    public NumberToken(int line, int column, String value) {
        super(line, column);
        this.value = value;
    }
    
    public String getValue() { return value; }
}
