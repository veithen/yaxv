package net.sf.yaxv.css;

import java.io.IOException;

public class Lexer {
	private final StreamConsumer in;
	
	public Lexer(StreamConsumer in) {
		this.in = in;
	}
	
	public Token readToken() throws IOException {
		int nextChar = in.nextChar();
		if (('a' <= nextChar && nextChar <= 'z')
		       || ('A' <= nextChar && nextChar <= 'Z')
			   || nextChar > 0177 || nextChar == '\\') {
			return readIdentifier();
		}
	}
	
	private Identifier readIdentifier() throws IOException {
		StringBuffer buff = new StringBuffer();
		while (true) {
			int nextChar = in.nextChar();
			if (('a' <= nextChar && nextChar <= 'z')
				   || ('A' <= nextChar && nextChar <= 'Z')
				   || ('0' <= nextChar && nextChar <= '9')
				   || nextChar == '-' || nextChar > 0177) {
				buff.append(nextChar);
				in.consume();
			} else if (nextChar == '\\') {
				buff.append(readEscape());
			} else {
				return new Identifier(buff.toString());
			}
		}
	}
	
	private char readEscape() throws IOException {
		in.consume();
		int nextChar = in.nextChar();
		if (('0' <= nextChar && nextChar <= '9') || ('a' <= nextChar && nextChar <= 'z') || ('A' <= nextChar && nextChar <= 'Z')) {
			
		} else if ((' ' <= nextChar && nextChar <= '~') || nextChar >= 0200) {
			in.consume();
			return (char)nextChar;
		} else {
			// TODO: throw exception
		}
	}
}
