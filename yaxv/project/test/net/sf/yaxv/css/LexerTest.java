package net.sf.yaxv.css;

import java.io.IOException;
import java.io.StringReader;
import junit.framework.TestCase;
import net.sf.yaxv.css.token.AtKeyword;
import net.sf.yaxv.css.token.Dot;
import net.sf.yaxv.css.token.Identifier;
import net.sf.yaxv.css.token.NumberToken;
import net.sf.yaxv.css.token.StringToken;

public class LexerTest extends TestCase {
	private Lexer createLexer(String css) {
		return new Lexer(new StreamConsumer(new StringReader(css)));
	}
	
	private Token parseToken(String token) throws IOException, CSSParserException {
		return createLexer(token).readToken();
	}
	
	private void testInvalid(String css) throws IOException {
		try {
			parseToken(css);
			fail("Expected parser exception");
		}
		catch (CSSParserException ex) {
			// OK
		}
	}
	
	private void testIdentifier(String css, String identifier) throws IOException, CSSParserException {
		Token token = parseToken(css);
		assertTrue(token instanceof Identifier);
		assertEquals(identifier, ((Identifier)token).getName());
	}
	
	public void testIdentifierWithEscapeChar() throws IOException, CSSParserException {
		testIdentifier("test\\ case", "test case");
	}
	
	public void testIdentifierWithUnicodeEscape() throws IOException, CSSParserException {
		testIdentifier("test\\a5bF case", "test\uA5BFcase");
	}
	
	public void testAtKeyword() throws IOException, CSSParserException {
		Token token = parseToken("@char-set");
		assertTrue(token instanceof AtKeyword);
		assertEquals("char-set", ((AtKeyword)token).getName());
	}
	
	private void testString(String css, String content) throws IOException, CSSParserException {
		Token token = parseToken(css);
		assertTrue(token instanceof StringToken);
		assertEquals(content, ((StringToken)token).getContent());
	}
	
	public void testString1() throws IOException, CSSParserException {
		testString("\"Test string\"", "Test string");
	}
	
	public void testString2() throws IOException, CSSParserException {
		testString("'Test string'", "Test string");
	}
	
	public void testString1WithSimpleQuote() throws IOException, CSSParserException {
		testString("\"D'habitude je ne le fais pas\"", "D'habitude je ne le fais pas");
	}
	
	public void testString2WithDoubleQuote() throws IOException, CSSParserException {
		testString("'He said \"Hello\"'", "He said \"Hello\"");
	}
	
	public void testStringWithEscapedNewlineN() throws IOException, CSSParserException {
		testString("'Test\\\nstring'", "Test\nstring");
	}
	
	public void testStringWithEscapedNewlineRN() throws IOException, CSSParserException {
		testString("'Test\\\r\nstring'", "Test\nstring");
	}
	
	public void testStringWithEscapedNewlineRF() throws IOException, CSSParserException {
		testString("'Test\\\r\fstring'", "Test\nstring");
	}
	
	private void testNumber(String css, String value) throws IOException, CSSParserException {
		Token token = parseToken(css);
		assertTrue(token instanceof NumberToken);
		assertEquals(value, ((NumberToken)token).getValue());
	}
	
	public void testNumberInteger() throws IOException, CSSParserException {
		testNumber("16842", "16842");
	}
	
	public void testNumberDecimal() throws IOException, CSSParserException {
		testNumber("16.842", "16.842");
	}
	
	public void testNumberDecimalStartingWithDot() throws IOException, CSSParserException {
		testNumber(".842", ".842");
	}
	
	public void testInvalidNumber() throws IOException {
		testInvalid("12.");
	}
	
	public void testDot() throws IOException, CSSParserException {
		assertTrue(parseToken(".") instanceof Dot);
	}
}
