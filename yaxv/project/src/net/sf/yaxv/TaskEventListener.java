package net.sf.yaxv;

import java.net.URI;
import java.text.MessageFormat;
import org.apache.tools.ant.Task;

public class TaskEventListener {
    private final Task task;
    private final URI base;
    
    public TaskEventListener(Task task) {
        this.task = task;
        base = task.getProject().getBaseDir().toURI();
    }
    
    public void event(String fileName, int line, int column, String key, Object[] args) {
        task.log(fileName + ":" + line + ":" + column + " " + MessageFormat.format(Messages.BUNDLE.getString(key), args) + " (" + key + ")");
    }
    
    public void event(URI uri, int line, int column, String key, Object[] args) {
        event(base.relativize(uri).toString(), line, column, key, args);
    }
}
