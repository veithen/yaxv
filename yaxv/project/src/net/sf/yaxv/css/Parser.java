package net.sf.yaxv.css;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import net.sf.yaxv.Messages;
import net.sf.yaxv.css.selector.AdjacentSelector;
import net.sf.yaxv.css.selector.BaseSelector;
import net.sf.yaxv.css.selector.ChildSelector;
import net.sf.yaxv.css.selector.ClassSelector;
import net.sf.yaxv.css.selector.DescendantSelector;
import net.sf.yaxv.css.selector.IdSelector;
import net.sf.yaxv.css.selector.PseudoClassSelector;
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
import net.sf.yaxv.css.token.Function;
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
    private static class ParserState {
        private final TokenConsumer in;
        private final ParserEventListener listener;
        
        public ParserState(TokenConsumer in, ParserEventListener listener) {
            this.in = in;
            this.listener = listener;
        }
        
        private void event(int level, Token token, String key) throws CSSParserException {
            int line = token.getLineNumber();
            int column = token.getColumnNumber();
            if (listener.event(level, line, column, key) == ParserEventListener.ACTION_STOP || level == ParserEventListener.LEVEL_FATAL_ERROR) {
                throw new CSSParserException(line, column, key);
            }
        }

        private void event(Token token, String key) throws CSSParserException {
            event(ParserEventListener.LEVEL_EVENT, token, key);
        }

        private void error(Token token, String key) throws CSSParserException {
            event(ParserEventListener.LEVEL_ERROR, token, key);
        }

        private void fatalError(Token token, String key) throws CSSParserException {
            event(ParserEventListener.LEVEL_FATAL_ERROR, token, key);
        }

        private String getUnexpectedTokenKey(Class tokenClass) {
            String name = tokenClass.getName();
            return "css.unexpected." + name.substring(name.lastIndexOf('.')+1).toLowerCase();
        }

        private void fatalUnexpectedToken(Token token) throws CSSParserException {
            fatalError(token, getUnexpectedTokenKey(token.getClass()));
        }

        public Stylesheet parseStylesheet() throws IOException, CSSParserException {
            Token nextToken = in.nextToken();
            if (nextToken instanceof AtKeyword && ((AtKeyword)nextToken).getName().equalsIgnoreCase("charset")) {
                in.consume();
                consumeSpace();
                String charset = expectString();
                consumeSpace();
                expectToken(Semicolon.class);
                // TODO: set charset in StreamConsumer
            }
            consumeSpaceAndCD();
            while (true) {
                nextToken = in.nextToken();
                if (nextToken instanceof AtKeyword && ((AtKeyword)nextToken).getName().equalsIgnoreCase("import")) {
                    parseImport();
                    consumeSpaceAndCD();
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
                        parseMedia();
                    } else if (name.equalsIgnoreCase("page")) {
                        parsePage();
                    } else if (name.equalsIgnoreCase("font-face")) {
                        parseFontFace();
                    } else {
                        throw new UnexpectedTokenException(nextToken);
                    }
                } else if (nextToken instanceof EOF) {
                    break;
                } else {
                    rulesets.add(parseRuleset());
                }
                consumeSpaceAndCD();
            }
            return new Stylesheet((Ruleset[])rulesets.toArray(new Ruleset[rulesets.size()]));
        }

        private void parseImport() throws IOException, CSSParserException {
            throw new UnsupportedOperationException();
        }

        private void parseMedia() throws IOException, CSSParserException {
            throw new UnsupportedOperationException();
        }

        private void parsePage() throws IOException, CSSParserException {
            throw new UnsupportedOperationException();
        }

        private void parseFontFace() throws IOException, CSSParserException {
            throw new UnsupportedOperationException();
        }

        private Ruleset parseRuleset() throws IOException, CSSParserException {
            List selectors = new LinkedList();
            selectors.add(parseSelector());
            Token nextToken;
            while ((nextToken = in.nextToken()) instanceof Comma) {
                in.consume();
                consumeSpace();
                selectors.add(parseSelector());
            }
            expectToken(LBrace.class);

            ruleset: while (true) {
                consumeSpace();
                String property = expectIdentifier();
                consumeSpace();
                expectToken(Colon.class);
                while (true) {
                    nextToken = in.nextToken();
                    if (nextToken instanceof RBrace) {
                        in.consume();
                        break ruleset;
                    } else if (nextToken instanceof Semicolon) {
                        in.consume();
                        break;
                    } else {
                        in.consume();
                    }
                }
                consumeSpace();
                nextToken = in.nextToken();
                if (nextToken instanceof RBrace) {
                    in.consume();
                    error(nextToken, Messages.CSS_RULESET_TERMINATED_BY_SEMICOLON);
                    break;
                }
            }

            return new Ruleset((Selector[])selectors.toArray(new Selector[selectors.size()]));
        }

        private Selector parseSelector() throws IOException, CSSParserException {
            Selector selector = parseSimpleSelector();
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
                    consumeSpace();
                }
                Selector selector2 = parseSimpleSelector();
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

        private Selector parseSimpleSelector() throws IOException, CSSParserException {
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
                    components.add(new ClassSelector(expectIdentifier()));
                } else if (nextToken instanceof LBracket) {
                    // TODO: attribute selector
                    throw new UnsupportedOperationException();
                } else if (nextToken instanceof Colon) {
                    in.consume();
                    nextToken = in.nextToken();
                    if (nextToken instanceof Identifier) {
                        in.consume();
                        components.add(new PseudoClassSelector(0)); // TODO: choose type from identifier
                    } else if (nextToken instanceof Function) {
                        throw new UnsupportedOperationException("Pseudo class selector with function");
                    } else {
                        throw new UnexpectedTokenException(nextToken);
                    }
                } else {
                    break;
                }
            }
            consumeSpace();
            if (components.isEmpty()) {
                return baseSelector == null ? null : baseSelector;
            } else if (baseSelector == null && components.size() == 1) {
                return (Selector)components.get(0);
            } else {
                return new SimpleSelector(baseSelector, (SimpleSelectorComponent[])components.toArray(new SimpleSelectorComponent[components.size()]));
            }
        }

        private void consumeSpace() throws IOException, CSSParserException {
            while (in.nextToken() instanceof Space) {
                in.consume();
            }
        }

        private void consumeSpaceAndCD() throws IOException, CSSParserException {
            while (true) {
                Token nextToken = in.nextToken();
                if (nextToken instanceof Space || nextToken instanceof CDO || nextToken instanceof CDC) {
                    in.consume();
                } else {
                    return;
                }
            }
        }

        private String expectString() throws IOException, CSSParserException {
            Token nextToken = in.nextToken();
            if (nextToken instanceof StringToken) {
                in.consume();
                return ((StringToken)nextToken).getContent();
            } else {
                throw new UnexpectedTokenException(nextToken);
            }
        }

        private String expectIdentifier() throws IOException, CSSParserException {
            Token nextToken = in.nextToken();
            if (nextToken instanceof Identifier) {
                in.consume();
                return ((Identifier)nextToken).getName();
            } else {
                throw new UnexpectedTokenException(nextToken);
            }
        }

        private void expectToken(Class tokenClass) throws IOException, CSSParserException {
            Token nextToken = in.nextToken();
            if (tokenClass.isInstance(nextToken)) {
                in.consume();
            } else {
                throw new UnexpectedTokenException(nextToken);
            }
        }
    }
    
    private final static ParserEventListener DEFAULT_LISTENER = new DefaultParserEventListener(); // TODO: never used

    public Stylesheet parseStylesheet(InputStream in, ParserEventListener listener) throws IOException, CSSParserException {
        // TODO: simply using InputStreamReader with the platform default charset is of course not correct...
        return parseStylesheet(new TokenConsumer(new Lexer(new StreamConsumer(new InputStreamReader(in)))), listener);
    }
    
    public Stylesheet parseStylesheet(TokenConsumer in, ParserEventListener listener) throws IOException, CSSParserException {
        return new ParserState(in, listener).parseStylesheet();
    }
}
