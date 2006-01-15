package net.sf.yaxv.css;

public class NumberToken extends Token {
	private final String value;
	
	public NumberToken(String value) {
		this.value = value;
	}
	
	public String getValue() { return value; }
}
