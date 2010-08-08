package com.googlecode.yaxv;

import com.googlecode.yaxv.url.LinkValidationEventListener;

public class AntLinkValidationEventListener implements LinkValidationEventListener {
    private final FileEventListener listener;
    private final int line;
    private final int column;
    
    public AntLinkValidationEventListener(FileEventListener listener, int line, int column) {
        this.listener = listener;
        this.line = line;
        this.column = column;
    }

    public void event(String key, Object[] args) {
        listener.event(line, column, key, args);
    }
}
