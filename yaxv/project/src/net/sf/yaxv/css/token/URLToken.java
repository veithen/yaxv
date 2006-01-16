package net.sf.yaxv.css.token;

import net.sf.yaxv.css.Token;

public class URLToken extends Token {
	private final String url;
	
	public URLToken(int line, int column, String url) {
		super(line, column);
		this.url = url;
	}
	
	public String getUrl() { return url; }
}
