package net.sf.yaxv.css;

public interface ParserEventListener {
	final static int LEVEL_EVENT = 0;
	final static int LEVEL_ERROR = 1;
	final static int LEVEL_FATAL_ERROR = 2;
	
	final static int ACTION_STOP = 0;
	final static int ACTION_CONTINUE = 1;
	
	int event(int level, int line, int column, String key);
}
