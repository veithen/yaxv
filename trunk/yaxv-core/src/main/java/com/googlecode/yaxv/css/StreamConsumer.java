package com.googlecode.yaxv.css;

import java.io.IOException;
import java.io.Reader;

public class StreamConsumer {
    private final Reader in;
    private int line = 1;
    private int column = 1;
    private int lastChar = -2;
    private int nextChar = -2;
    
    public StreamConsumer(Reader in) {
        this.in = in;
    }
    
    public int nextChar() throws IOException {
        if (nextChar == -2) {
            nextChar = in.read();
        }
        return nextChar;
    }
    
    public void consume() throws IOException {
        if (nextChar == -2) {
            throw new IllegalStateException("Cannot consume a char that has not yet been read");
        }
        if (nextChar == -1) {
            throw new IllegalStateException("Consuming EOF character");
        }
        if (nextChar == '\n' || (lastChar == '\r' && nextChar == '\f')) {
            line++;
            column = 1;
        } else {
            column++;
        }
        lastChar = nextChar;
        nextChar = -2;
    }
    
    public int getLineNumber() { return line; }
    public int getColumnNumber() { return column; }
}
