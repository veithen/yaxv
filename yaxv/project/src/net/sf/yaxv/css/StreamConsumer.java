package net.sf.yaxv.css;

import java.io.IOException;
import java.io.Reader;

public class StreamConsumer {
	private final Reader in;
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
	
	public int consume() throws IOException {
		int result = nextChar();
		nextChar = -2;
		return result;
	}
}
