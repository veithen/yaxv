package com.googlecode.yaxv.css;

public class UnexpectedTokenException extends CSSParserException {
    private static final long serialVersionUID = 8731254353332259536L;

    public UnexpectedTokenException(Token token) {
        super(token.getLineNumber(), token.getColumnNumber(), getKey(token.getClass()));
    }
    
    private static String getKey(Class tokenClass) {
        String name = tokenClass.getName();
        return "css.unexpected." + name.substring(name.lastIndexOf('.')+1).toLowerCase();
    }
}
