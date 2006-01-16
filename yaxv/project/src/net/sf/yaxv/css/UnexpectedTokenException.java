package net.sf.yaxv.css;

public class UnexpectedTokenException extends CSSParserException {
	public UnexpectedTokenException(Token token) {
		super(token.getLineNumber(), token.getColumnNumber(), getKey(token.getClass()));
	}
	
	private static String getKey(Class tokenClass) {
		String name = tokenClass.getName();
		return "css.unexpected." + name.substring(name.lastIndexOf('.')+1).toLowerCase();
	}
}
