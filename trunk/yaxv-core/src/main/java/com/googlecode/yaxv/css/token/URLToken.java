package com.googlecode.yaxv.css.token;

import com.googlecode.yaxv.css.Token;

public class URLToken extends Token {
    private final String url;
    
    public URLToken(int line, int column, String url) {
        super(line, column);
        this.url = url;
    }
    
    public String getUrl() { return url; }
}
