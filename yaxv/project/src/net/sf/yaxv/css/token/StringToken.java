package net.sf.yaxv.css.token;

import net.sf.yaxv.css.Token;

public class StringToken extends Token {
    private final String content;
    
    public StringToken(int line, int column, String content) {
        super(line, column);
        this.content = content;
    }
    
    public String getContent() { return content; }
}
