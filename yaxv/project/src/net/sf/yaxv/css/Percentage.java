package net.sf.yaxv.css;

public class Percentage extends Token {
	private final String value;
	
	public Percentage(String value) {
		this.value = value;
	}
	
	public String getValue() { return value; }
}
