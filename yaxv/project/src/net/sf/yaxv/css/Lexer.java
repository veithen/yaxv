package net.sf.yaxv.css;

import java.io.IOException;

public class Lexer {
	private final StreamConsumer in;
	
	public Lexer(StreamConsumer in) {
		this.in = in;
	}
	
	public Token readToken() throws IOException, CSSParserException {
		int nextChar = in.nextChar();
		if (is_nmstart(nextChar)) {
			return new Identifier(readIdent());
		} else if (nextChar == '@') {
			in.consume();
			String ident = readIdent();
			if (ident.length() == 0) {
				throw new CSSParserException("Identifier expected after @");
			}
			return new AtKeyword(ident);
		} else if (nextChar == '"' || nextChar == '\'') { 
			return readString();
		} else if (('0' <= nextChar && nextChar <= '9') || nextChar == '.') { 
			String num = readNum();
			nextChar = in.nextChar();
			if (nextChar == '%') {
				in.consume();
				return new Percentage(num);
			} else if (is_nmstart(nextChar)) {
				return new Dimension(num, readIdent());
			} else {
				return new NumberToken(num);
			}
		} else {
			throw new CSSParserException("Unexpected character");
		}
	}
	
	private boolean is_nonascii(int c) { return c > 0177; }
	
	private boolean is_nmstart(int c) {
		return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || is_nonascii(c) || c == '\\';
	}
	
	private String readIdent() throws IOException, CSSParserException {
		StringBuffer buff = new StringBuffer();
		while (true) {
			int nextChar = in.nextChar();
			if (('a' <= nextChar && nextChar <= 'z')
				   || ('A' <= nextChar && nextChar <= 'Z')
				   || ('0' <= nextChar && nextChar <= '9')
				   || nextChar == '-' || is_nonascii(nextChar)) {
				buff.append((char)nextChar);
				in.consume();
			} else if (nextChar == '\\') {
				in.consume();
				buff.append(readUnicodeOrEscapeSequence());
			} else {
				return buff.toString();
			}
		}
	}
	
	private char readUnicodeOrEscapeSequence() throws IOException, CSSParserException {
		int nextChar = in.nextChar();
		if (('0' <= nextChar && nextChar <= '9') || ('a' <= nextChar && nextChar <= 'z') || ('A' <= nextChar && nextChar <= 'Z')) {
			int result = 0;
			int digits = 0;
			while (digits < 6) {
				int nibble;
				if ('0' <= nextChar && nextChar <= '9') {
					nibble = nextChar - '0';
				} else if ('a' <= nextChar && nextChar <= 'z') {
					nibble = nextChar - 'a' + 10;
				} else if ('A' <= nextChar && nextChar <= 'Z') {
					nibble = nextChar - 'A' + 10;
				} else if (nextChar == ' ' || nextChar == '\n' || nextChar == '\r' || nextChar == '\t' || nextChar == '\f') {
					in.consume();
					break;
				} else {
					break;
				}
				result = (result << 4) + nibble;
				digits++;
				in.consume();
				nextChar = in.nextChar();
			}
			// TODO: Java supports only 16 bit (4 digit) unicode chars
			return (char)result;
		} else if ((' ' <= nextChar && nextChar <= '~') || nextChar >= 0200) {
			in.consume();
			return (char)nextChar;
		} else {
			throw new CSSParserException("Invalid escape char");
		}
	}
	
	private StringToken readString() throws IOException, CSSParserException {
		StringBuffer buff = new StringBuffer();
		boolean doubleQuoted = in.consume() == '"';
		while (true) {
			int nextChar = in.nextChar();
			if (nextChar == '\\') {
				in.consume();
				nextChar = in.nextChar();
				if (nextChar == '\n') {
					in.consume();
					buff.append('\n');
				} else if (nextChar == '\r') {
					in.consume();
					nextChar = in.nextChar();
					if (nextChar == '\n' || nextChar == '\f') {
						in.consume();
						buff.append('\n');
					} else {
						throw new CSSParserException("Invalid newline in escape sequence");
					}
				} else {
					buff.append(readUnicodeOrEscapeSequence());
				}
			} else if (nextChar == '\t' || nextChar == ' ' || nextChar == '!' || nextChar == '#' || nextChar == '$'
				 || nextChar == '%' || nextChar == '&' || ('(' <= nextChar && nextChar <= '~') || is_nonascii(nextChar)
				 || (!doubleQuoted && nextChar == '"') || (doubleQuoted && nextChar == '\'')) {
				in.consume();
				buff.append((char)nextChar);
			} else if (nextChar == '"' || nextChar == '\'') {
				in.consume();
				return new StringToken(buff.toString());
			} else if (nextChar == -1) {
				throw new CSSParserException("Unterminated string literal");
			} else {
				throw new CSSParserException("Unexpected character (code " + nextChar + ") in string literal");
			}
		}
	}
	
	private String readNum() throws IOException, CSSParserException {
		StringBuffer buff = new StringBuffer();
		boolean dot = false;
		while (true) {
			int nextChar = in.nextChar();
			if (('0' <= nextChar && nextChar <= '9') || (!dot && nextChar == '.')) {
				in.consume();
				buff.append((char)nextChar);
			} else {
				return buff.toString();
			}
		}
	}
}
