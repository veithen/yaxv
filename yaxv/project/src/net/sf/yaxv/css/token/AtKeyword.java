package net.sf.yaxv.css.token;

import net.sf.yaxv.css.Token;

public class AtKeyword extends Token {
	private final String name;
	
	public AtKeyword(String name) {
		this.name = name;
	}
	
	public String getName() { return name; }
}
