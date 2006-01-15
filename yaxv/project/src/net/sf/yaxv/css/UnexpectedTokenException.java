package net.sf.yaxv.css;

public class UnexpectedTokenException extends CSSParserException {
	public UnexpectedTokenException(Token token) {
		super("Unexpected token of type " + token.getClass().getName());
	}
}
