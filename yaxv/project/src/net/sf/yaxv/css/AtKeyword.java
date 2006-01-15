package net.sf.yaxv.css;

public class AtKeyword extends Token {
	private final String name;
	
	public AtKeyword(String name) {
		this.name = name;
	}
	
	public String getName() { return name; }
}
