package com.googlecode.yaxv;

import java.text.MessageFormat;

public class FileEventListener {
    private final TaskEventListener parent;
    private final String fileName;
    
    public FileEventListener(TaskEventListener parent, String fileName) {
        this.parent = parent;
        this.fileName = fileName;
    }
    
    public void event(int line, int column, String key) {
        event(line, column, key, new Object[0]);
    }
    
    public void event(int line, int column, String key, Object[] args) {
        parent.event(fileName, line, column, key, args);
    }
}
