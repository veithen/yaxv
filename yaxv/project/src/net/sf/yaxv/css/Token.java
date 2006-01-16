package net.sf.yaxv.css;

public class Token {
	private final int line;
	private final int column;
	
	public Token(int line, int column) {
		this.line = line;
		this.column = column;
	}
	
	public int getLineNumber() { return line; }
	public int getColumnNumber() { return column; }
}
