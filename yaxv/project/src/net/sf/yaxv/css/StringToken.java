package net.sf.yaxv.css;

public class StringToken extends Token {
	private final String content;
	
	public StringToken(String content) {
		this.content = content;
	}
	
	public String getContent() { return content; }
}
