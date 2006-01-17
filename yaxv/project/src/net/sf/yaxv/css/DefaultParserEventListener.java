package net.sf.yaxv.css;

public class DefaultParserEventListener implements ParserEventListener {
	public int event(int level, int line, int column, String key) {
		return level == LEVEL_EVENT ? ACTION_CONTINUE : ACTION_STOP;
	}
}
