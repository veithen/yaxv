package net.sf.yaxv.css.token;

import net.sf.yaxv.css.Token;

public class Hash extends Token {
	private final String name;
	
	public Hash(int line, int column, String name) {
		super(line, column);
		this.name = name;
	}
	
	public String getName() { return name; }
}
