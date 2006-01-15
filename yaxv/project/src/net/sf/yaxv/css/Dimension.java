package net.sf.yaxv.css;

public class Dimension extends Token {
	private final String value;
	private final String unit;
	
	public Dimension(String value, String unit) {
		this.value = value;
		this.unit = unit;
	}
	
	public String getValue() { return value; }
	public String getUnit() { return unit; }
}
