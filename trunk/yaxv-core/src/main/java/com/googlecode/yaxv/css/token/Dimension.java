package com.googlecode.yaxv.css.token;

import com.googlecode.yaxv.css.Token;

public class Dimension extends Token {
    private final String value;
    private final String unit;
    
    public Dimension(int line, int column, String value, String unit) {
        super(line, column);
        this.value = value;
        this.unit = unit;
    }
    
    public String getValue() { return value; }
    public String getUnit() { return unit; }
}
