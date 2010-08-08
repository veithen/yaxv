package net.sf.yaxv;

import org.apache.tools.ant.Task;

public class ErrorListener {
    public final static int WARNING = 1;
    public final static int ERROR = 2;
    public final static int FATAL = 3;
    
    private final Task task;
    private final String file;
    private int errorCount;
    
    public ErrorListener(Task task, String file) {
        this.task = task;
        this.file = file;
    }
    
    public void log(int level, int line, int column, String message) {
        String tag;
        switch (level) {
            case WARNING: tag = "warning"; break;
            case ERROR: tag = "error"; break;
            case FATAL: tag = "fatal"; break;
            default: tag = "???";
        }
        task.log("[" + tag + "] " + file + ":" + line + ":" + column + " " + message);
        if (level >= ERROR) {
            errorCount++;
        }
    }
    
    public int getErrorCount() { return errorCount; }
}
