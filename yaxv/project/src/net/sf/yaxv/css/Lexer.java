package net.sf.yaxv.css;

import java.io.IOException;
import net.sf.yaxv.css.selector.RBrace;
import net.sf.yaxv.css.token.Asterisk;
import net.sf.yaxv.css.token.AtKeyword;
import net.sf.yaxv.css.token.CDC;
import net.sf.yaxv.css.token.CDO;
import net.sf.yaxv.css.token.ChildOf;
import net.sf.yaxv.css.token.Colon;
import net.sf.yaxv.css.token.Comma;
import net.sf.yaxv.css.token.Dimension;
import net.sf.yaxv.css.token.EOF;
import net.sf.yaxv.css.token.Hash;
import net.sf.yaxv.css.token.Identifier;
import net.sf.yaxv.css.token.LBrace;
import net.sf.yaxv.css.token.LBracket;
import net.sf.yaxv.css.token.NumberToken;
import net.sf.yaxv.css.token.Percentage;
import net.sf.yaxv.css.token.Plus;
import net.sf.yaxv.css.token.Semicolon;
import net.sf.yaxv.css.token.Space;
import net.sf.yaxv.css.token.StringToken;

public class Lexer {
	private final StreamConsumer in;
	
	public Lexer(StreamConsumer in) {
		this.in = in;
	}
	
	public Token readToken() throws IOException, CSSParserException {
		int nextChar = in.nextChar();
		if (is_nmstart(nextChar)) {
			return new Identifier(readIdentOrName());
		} else if (nextChar == '@') {
			in.consume();
			if (is_nmstart(in.nextChar())) {
				return new AtKeyword(readIdentOrName());
			} else {
				throw new CSSParserException("Identifier expected after @");
			}
		} else if (nextChar == '#') { 
			in.consume();
			if (is_nmchar(in.nextChar())) {
				return new Hash(readIdentOrName());
			} else {
				throw new CSSParserException("Name expected after #");
			}
		} else if (nextChar == '"' || nextChar == '\'') { 
			return readString();
		} else if (('0' <= nextChar && nextChar <= '9') || nextChar == '.') { 
			String num = readNum();
			nextChar = in.nextChar();
			if (nextChar == '%') {
				in.consume();
				return new Percentage(num);
			} else if (is_nmstart(nextChar)) {
				return new Dimension(num, readIdentOrName());
			} else {
				return new NumberToken(num);
			}
		} else if (is_w(nextChar)) {
			do {
				in.consume();
			} while (is_w(in.nextChar()));
			return new Space();
		} else if (nextChar == ';') {
			in.consume();
			return new Semicolon();
		} else if (nextChar == ':') {
			in.consume();
			return new Colon();
		} else if (nextChar == ',') {
			in.consume();
			return new Comma();
		} else if (nextChar == '[') {
			in.consume();
			return new LBracket();
		} else if (nextChar == '{') {
			in.consume();
			return new LBrace();
		} else if (nextChar == '}') {
			in.consume();
			return new RBrace();
		} else if (nextChar == '+') {
			in.consume();
			return new Plus();
		} else if (nextChar == '>') {
			in.consume();
			return new ChildOf();
		} else if (nextChar == '<') {
			in.consume();
			expectChar('!');
			expectChar('-');
			expectChar('-');
			return new CDO();
		} else if (nextChar == '-') {
			in.consume();
			expectChar('-');
			expectChar('>');
			return new CDC();
		} else if (nextChar == '*') {
			in.consume();
			return new Asterisk();
		} else if (nextChar == -1) {
			return new EOF();
		} else {
			throw new CSSParserException("Unexpected character");
		}
	}
	
	private void expectChar(int c) throws IOException, CSSParserException {
		int nextChar = in.nextChar();
		if (nextChar == c) {
			in.consume();
		} else {
			throw new CSSParserException("Unexpected character");
		}
	}
	
	private boolean is_w(int c) {
		return c == ' ' || c == '\t' || c == '\r' || c == '\n' || c == '\f';
	}
	
	private boolean is_nonascii(int c) { return c > 0177; }
	
	private boolean is_nmstart(int c) {
		return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || is_nonascii(c) || c == '\\';
	}
	
	private boolean is_nmchar(int c) {
		return is_nmstart(c) || ('0' <= c && c <= '9') || c == '-';
	}
	
	private String readIdentOrName() throws IOException, CSSParserException {
		StringBuffer buff = new StringBuffer();
		while (true) {
			int nextChar = in.nextChar();
			if (is_nmchar(nextChar)) {
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
				} else if (is_w(nextChar)) {
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
