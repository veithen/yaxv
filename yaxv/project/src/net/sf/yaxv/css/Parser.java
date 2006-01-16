package net.sf.yaxv.css;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import net.sf.yaxv.css.selector.AdjacentSelector;
import net.sf.yaxv.css.selector.BaseSelector;
import net.sf.yaxv.css.selector.ChildSelector;
import net.sf.yaxv.css.selector.ClassSelector;
import net.sf.yaxv.css.selector.DescendantSelector;
import net.sf.yaxv.css.selector.IdSelector;
import net.sf.yaxv.css.selector.SimpleSelector;
import net.sf.yaxv.css.selector.SimpleSelectorComponent;
import net.sf.yaxv.css.selector.TypeSelector;
import net.sf.yaxv.css.selector.UniversalSelector;
import net.sf.yaxv.css.token.Asterisk;
import net.sf.yaxv.css.token.AtKeyword;
import net.sf.yaxv.css.token.CDC;
import net.sf.yaxv.css.token.CDO;
import net.sf.yaxv.css.token.ChildOf;
import net.sf.yaxv.css.token.Colon;
import net.sf.yaxv.css.token.Comma;
import net.sf.yaxv.css.token.Dot;
import net.sf.yaxv.css.token.EOF;
import net.sf.yaxv.css.token.Hash;
import net.sf.yaxv.css.token.Identifier;
import net.sf.yaxv.css.token.LBrace;
import net.sf.yaxv.css.token.LBracket;
import net.sf.yaxv.css.token.Plus;
import net.sf.yaxv.css.token.RBrace;
import net.sf.yaxv.css.token.Semicolon;
import net.sf.yaxv.css.token.Space;
import net.sf.yaxv.css.token.StringToken;

public class Parser {
	public Stylesheet parseStylesheet(InputStream in) throws IOException, CSSParserException {
		// TODO: simply using InputStreamReader with the platform default charset is of course not correct...
		return parseStylesheet(new TokenConsumer(new Lexer(new StreamConsumer(new InputStreamReader(in)))));
	}
	
	public Stylesheet parseStylesheet(TokenConsumer in) throws IOException, CSSParserException {
		Token nextToken = in.nextToken();
		if (nextToken instanceof AtKeyword && ((AtKeyword)nextToken).getName().equalsIgnoreCase("charset")) {
			in.consume();
			consumeSpace(in);
			String charset = expectString(in);
			consumeSpace(in);
			expectToken(in, Semicolon.class);
			// TODO: set charset in StreamConsumer
		}
		consumeSpaceAndCD(in);
		while (true) {
			nextToken = in.nextToken();
			if (nextToken instanceof AtKeyword && ((AtKeyword)nextToken).getName().equalsIgnoreCase("import")) {
				parseImport(in);
				consumeSpaceAndCD(in);
			} else {
				break;
			}
		}
		List rulesets = new LinkedList();
		while (true) {
			nextToken = in.nextToken();
			if (nextToken instanceof AtKeyword) {
				String name = ((AtKeyword)nextToken).getName();
				if (name.equalsIgnoreCase("media")) {
					parseMedia(in);
				} else if (name.equalsIgnoreCase("page")) {
					parsePage(in);
				} else if (name.equalsIgnoreCase("font-face")) {
					parseFontFace(in);
				} else {
					throw new UnexpectedTokenException(nextToken);
				}
			} else if (nextToken instanceof EOF) {
				break;
			} else {
				rulesets.add(parseRuleset(in));
			}
			consumeSpaceAndCD(in);
		}
		return new Stylesheet((Ruleset[])rulesets.toArray(new Ruleset[rulesets.size()]));
	}
	
	private void parseImport(TokenConsumer in) throws IOException, CSSParserException {
		
	}
	
	private void parseMedia(TokenConsumer in) throws IOException, CSSParserException {
		
	}
	
	private void parsePage(TokenConsumer in) throws IOException, CSSParserException {
		
	}
	
	private void parseFontFace(TokenConsumer in) throws IOException, CSSParserException {
		
	}
	
	private Ruleset parseRuleset(TokenConsumer in) throws IOException, CSSParserException {
		List selectors = new LinkedList();
		selectors.add(parseSelector(in));
		Token nextToken;
		while ((nextToken = in.nextToken()) instanceof Comma) {
			in.consume();
			consumeSpace(in);
			selectors.add(parseSelector(in));
		}
		expectToken(in, LBrace.class);
		
		// Skip the definition of the ruleset
		do {
			nextToken = in.consume();
		} while (!(nextToken instanceof RBrace));
		
		return new Ruleset((Selector[])selectors.toArray(new Selector[selectors.size()]));
	}
	
	private Selector parseSelector(TokenConsumer in) throws IOException, CSSParserException {
		Selector selector = parseSimpleSelector(in);
		if (selector == null) {
			throw new UnexpectedTokenException(in.nextToken());
		}
		while (true) {
			Token nextToken = in.nextToken();
			int type;
			if (nextToken instanceof Plus) {
				type = 1;
			} else if (nextToken instanceof ChildOf) {
				type = 2;
			} else {
				type = 0;
			}
			if (type != 0) {
				in.consume();
				consumeSpace(in);
			}
			Selector selector2 = parseSimpleSelector(in);
			if (selector2 == null) {
				if (type == 0) {
					return selector;
				} else {
					throw new UnexpectedTokenException(in.nextToken());
				}
			} else {
				switch (type) {
					case 0: selector = new DescendantSelector(selector, selector2); break;
					case 1: selector = new AdjacentSelector(selector, selector2); break;
					case 2: selector = new ChildSelector(selector, selector2); break;
				}
			}
		}
	}
	
	private Selector parseSimpleSelector(TokenConsumer in) throws IOException, CSSParserException {
		BaseSelector baseSelector;
		List components = new LinkedList();
		Token nextToken = in.nextToken();
		if (nextToken instanceof Identifier) {
			in.consume();
			baseSelector = new TypeSelector(((Identifier)nextToken).getName());
		} else if (nextToken instanceof Asterisk) {
			in.consume();
			baseSelector = new UniversalSelector();
		} else {
			baseSelector = null;
		}
		while (true) {
			nextToken = in.nextToken();
			if (nextToken instanceof Hash) {
				in.consume();
				components.add(new IdSelector(((Hash)nextToken).getName()));
			} else if (nextToken instanceof Dot) {
				in.consume();
				components.add(new ClassSelector(expectIdentifier(in)));
			} else if (nextToken instanceof LBracket) {
				// TODO: attribute selector
			} else if (nextToken instanceof Colon) {
				// TODO: pseudo class selector
			} else {
				break;
			}
		}
		consumeSpace(in);
		if (components.isEmpty()) {
			return baseSelector == null ? null : baseSelector;
		} else if (baseSelector == null && components.size() == 1) {
			return (Selector)components.get(0);
		} else {
			return new SimpleSelector(baseSelector, (SimpleSelectorComponent[])components.toArray(new SimpleSelectorComponent[components.size()]));
		}
	}
	
	private void consumeSpace(TokenConsumer in) throws IOException, CSSParserException {
		while (in.nextToken() instanceof Space) {
			in.consume();
		}
	}
	
	private void consumeSpaceAndCD(TokenConsumer in) throws IOException, CSSParserException {
		while (true) {
			Token nextToken = in.nextToken();
			if (nextToken instanceof Space || nextToken instanceof CDO || nextToken instanceof CDC) {
				in.consume();
			} else {
				return;
			}
		}
	}
	
	private String expectString(TokenConsumer in) throws IOException, CSSParserException {
		Token nextToken = in.nextToken();
		if (nextToken instanceof StringToken) {
			in.consume();
			return ((StringToken)nextToken).getContent();
		} else {
			throw new UnexpectedTokenException(nextToken);
		}
	}
	
	private String expectIdentifier(TokenConsumer in) throws IOException, CSSParserException {
		Token nextToken = in.nextToken();
		if (nextToken instanceof Identifier) {
			in.consume();
			return ((Identifier)nextToken).getName();
		} else {
			throw new UnexpectedTokenException(nextToken);
		}
	}
	
	private void expectToken(TokenConsumer in, Class tokenClass) throws IOException, CSSParserException {
		Token nextToken = in.nextToken();
		if (tokenClass.isInstance(nextToken)) {
			in.consume();
		} else {
			throw new UnexpectedTokenException(nextToken);
		}
	}
}
