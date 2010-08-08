package net.sf.yaxv.css;

import java.net.URI;

public interface StylesheetCacheEventListener {
    void event(URI uri, int line, int column, String key);
}
