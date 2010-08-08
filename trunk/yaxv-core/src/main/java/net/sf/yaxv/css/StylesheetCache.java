package net.sf.yaxv.css;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class StylesheetCache {
    private final Parser cssParser;
    private final StylesheetCacheEventListener listener;
    private final Map/*<URI,Stylesheet>*/ cache = new HashMap();
    
    public StylesheetCache(Parser cssParser, StylesheetCacheEventListener listener) {
        this.cssParser = cssParser;
        this.listener = listener;
    }
    
    public Parser getParser() { return cssParser; }
    
    public Stylesheet loadStylesheet(final URI uri) throws CSSParserException, IOException {
        Stylesheet stylesheet = (Stylesheet)cache.get(uri);
        if (stylesheet == null) {
            stylesheet = cssParser.parseStylesheet(uri.toURL().openStream(), new ParserEventListener() {
                public int event(int level, int line, int column, String key) {
                    listener.event(uri, line, column, key);
                    return ACTION_CONTINUE;
                }
            });
            // TODO: close stream
            cache.put(uri, stylesheet);
        }
        return stylesheet;
    }
}
