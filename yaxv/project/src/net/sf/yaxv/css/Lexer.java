package net.sf.yaxv.css;

import java.io.IOException;
import net.sf.yaxv.css.token.Asterisk;
import net.sf.yaxv.css.token.AtKeyword;
import net.sf.yaxv.css.token.CDC;
import net.sf.yaxv.css.token.CDO;
import net.sf.yaxv.css.token.ChildOf;
import net.sf.yaxv.css.token.Colon;
import net.sf.yaxv.css.token.Comma;
import net.sf.yaxv.css.token.Dimension;
import net.sf.yaxv.css.token.Dot;
import net.sf.yaxv.css.token.EOF;
import net.sf.yaxv.css.token.Hash;
import net.sf.yaxv.css.token.Identifier;
import net.sf.yaxv.css.token.LBrace;
import net.sf.yaxv.css.token.LBracket;
import net.sf.yaxv.css.token.Minus;
import net.sf.yaxv.css.token.NumberToken;
import net.sf.yaxv.css.token.Percentage;
import net.sf.yaxv.css.token.Plus;
import net.sf.yaxv.css.token.RBrace;
import net.sf.yaxv.css.token.Semicolon;
import net.sf.yaxv.css.token.Space;
import net.sf.yaxv.css.token.StringToken;
import net.sf.yaxv.css.token.URLToken;

public class Lexer {
	private final StreamConsumer in;
	
	public Lexer(StreamConsumer in) {
		this.in = in;
	}
	
	public Token readToken() throws IOException, CSSParserException {
		// Save the line and column number of the start of the token to be read; these
		// will be used when the token instance is created.
		// Note that for exceptions, we will usually use in.getLineNumber() and in.getColumnNumber().
		int line = in.getLineNumber();
		int column = in.getColumnNumber();
		
		int nextChar;
		while ((nextChar = in.nextChar()) == '/') {
			in.consume();
			expectChar('*');
			while (true) {
				nextChar = in.nextChar();
				if (nextChar == -1) {
					throw new CSSParserException(line, column, "css.unterminated.comment");
				} else if (nextChar == '*') {
					in.consume();
					if (in.nextChar() == '/') {
						in.consume();
						break;
					}
				} else {
					in.consume();
				}
			}
		}
		if (is_nmstart(nextChar)) {
			String identifier = readIdentOrName();
			if (identifier.equals("url") && in.nextChar() == '(') {
				in.consume();
				String url;
				while (is_w(in.nextChar())) {
					in.consume();
				}
				if (isQuote(in.nextChar())) {
					url = readString();
				} else {
					StringBuffer buff = new StringBuffer();
					while (true) {
						nextChar = in.nextChar();
						if (nextChar == -1) {
							throw new CSSParserException(line, column, "css.unterminated.url");
						} else if (nextChar == '\\') {
							in.consume();
							buff.append(readUnicodeOrEscapeSequence());
						} else if (nextChar == '!' || nextChar == '#' || nextChar == '$' || nextChar == '%' || nextChar == '&' || ('*' <= nextChar && nextChar <= '~') || is_nonascii(nextChar)) {
							in.consume();
							buff.append((char)nextChar);
						} else {
							break;
						}
					}
					url = buff.toString();
				}
				while (is_w(in.nextChar())) {
					in.consume();
				}
				expectChar(')');
				return new URLToken(line, column, url);
			} else {
				return new Identifier(line, column, identifier);
			}
		} else if (nextChar == '@') {
			in.consume();
			if (is_nmstart(in.nextChar())) {
				return new AtKeyword(line, column, readIdentOrName());
			} else {
				throw new CSSParserException(in.getLineNumber(), in.getColumnNumber(), "Identifier expected after @");
			}
		} else if (nextChar == '#') { 
			in.consume();
			if (is_nmchar(in.nextChar())) {
				return new Hash(line, column, readIdentOrName());
			} else {
				throw new CSSParserException(in.getLineNumber(), in.getColumnNumber(), "Name expected after #");
			}
		} else if (isQuote(nextChar)) { 
			return new StringToken(line, column, readString());
		} else if (('0' <= nextChar && nextChar <= '9') || nextChar == '.') {
			StringBuffer buff;
			boolean dot;
			if (nextChar == '.') {
				in.consume();
				nextChar = in.nextChar();
				if ('0' <= nextChar && nextChar <= '9') {
					buff = new StringBuffer(".");
					dot = true;
				} else {
					return new Dot(line, column);
				}
			} else {
				buff = new StringBuffer();
				dot = false;
			}
			while (true) {
				nextChar = in.nextChar();
				if (('0' <= nextChar && nextChar <= '9') || (!dot && nextChar == '.')) {
					in.consume();
					buff.append((char)nextChar);
				} else {
					break;
				}
			}
			if (buff.charAt(buff.length()-1) == '.') {
				throw new CSSParserException(line, column, "css.unterminated.decimal");
			}
			nextChar = in.nextChar();
			if (nextChar == '%') {
				in.consume();
				return new Percentage(line, column, buff.toString());
			} else if (is_nmstart(nextChar)) {
				return new Dimension(line, column, buff.toString(), readIdentOrName());
			} else {
				return new NumberToken(line, column, buff.toString());
			}
		} else if (is_w(nextChar)) {
			do {
				in.consume();
			} while (is_w(in.nextChar()));
			return new Space(line, column);
		} else if (nextChar == ';') {
			in.consume();
			return new Semicolon(line, column);
		} else if (nextChar == ':') {
			in.consume();
			return new Colon(line, column);
		} else if (nextChar == ',') {
			in.consume();
			return new Comma(line, column);
		} else if (nextChar == '[') {
			in.consume();
			return new LBracket(line, column);
		} else if (nextChar == '{') {
			in.consume();
			return new LBrace(line, column);
		} else if (nextChar == '}') {
			in.consume();
			return new RBrace(line, column);
		} else if (nextChar == '+') {
			in.consume();
			return new Plus(line, column);
		} else if (nextChar == '-') {
			in.consume();
			return new Minus(line, column);
		} else if (nextChar == '>') {
			in.consume();
			return new ChildOf(line, column);
		} else if (nextChar == '<') {
			in.consume();
			expectChar('!');
			expectChar('-');
			expectChar('-');
			return new CDO(line, column);
		} else if (nextChar == '-') {
			in.consume();
			expectChar('-');
			expectChar('>');
			return new CDC(line, column);
		} else if (nextChar == '*') {
			in.consume();
			return new Asterisk(line, column);
		} else if (nextChar == -1) {
			return new EOF(line, column);
		} else {
			throw new CSSParserException(in.getLineNumber(), in.getColumnNumber(), "css.unexpected.char");
		}
	}
	
	private void expectChar(int c) throws IOException, CSSParserException {
		int nextChar = in.nextChar();
		if (nextChar == c) {
			in.consume();
		} else {
			throw new CSSParserException(in.getLineNumber(), in.getColumnNumber(), "css.unexpected.char");
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
	
	private boolean isQuote(int c) {
		return c == '"' || c == '\'';
	}
	
	private String readIdentOrName() throws IOException, CSSParserException {
		StringBuffer buff = new StringBuffer();
		while (true) {
			int nextChar = in.nextChar();
			if (nextChar == '\\') {
				in.consume();
				buff.append(readUnicodeOrEscapeSequence());
			} else if (is_nmchar(nextChar)) {
				buff.append((char)nextChar);
				in.consume();
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
			throw new CSSParserException(in.getLineNumber(), in.getColumnNumber(), "Invalid escape char");
		}
	}
	
	private String readString() throws IOException, CSSParserException {
		StringBuffer buff = new StringBuffer();
		boolean doubleQuoted = in.nextChar() == '"';
		in.consume();
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
						throw new CSSParserException(in.getLineNumber(), in.getColumnNumber(), "Invalid newline in escape sequence");
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
				return buff.toString();
			} else if (nextChar == -1) {
				throw new CSSParserException(in.getLineNumber(), in.getColumnNumber(), "Unterminated string literal");
			} else {
				throw new CSSParserException(in.getLineNumber(), in.getColumnNumber(), "Unexpected character (code " + nextChar + ") in string literal");
			}
		}
	}
}
