package net.sf.yaxv;

import net.sf.yaxv.css.ParserEventListener;

public class AntParserEventListener implements ParserEventListener {
    private final FileEventListener listener;
    
    public AntParserEventListener(FileEventListener listener) {
        this.listener = listener;
    }
    
    public int event(int level, int line, int column, String key) {
        listener.event(line, column, key);
        return ACTION_CONTINUE;
    }
}
