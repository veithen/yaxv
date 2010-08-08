package net.sf.yaxv.css;

import java.io.IOException;

public class TokenConsumer {
    private final Lexer lexer;
    private Token nextToken;
    
    public TokenConsumer(Lexer lexer) {
        this.lexer = lexer;
    }
    
    public Token nextToken() throws IOException, CSSParserException {
        if (nextToken == null) {
            nextToken = lexer.readToken();
        }
        return nextToken;
    }
    
    public Token consume() throws IOException, CSSParserException {
        Token result = nextToken();
        nextToken = null;
        return result;
    }
}
