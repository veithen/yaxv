package net.sf.yaxv;

import java.text.MessageFormat;
import org.apache.tools.ant.Task;

public class FileEventListener {
	private final Task task;
	private final String fileName;
	
	public FileEventListener(Task task, String fileName) {
		this.task = task;
		this.fileName = fileName;
	}
	
	public void event(int line, int column, String key) {
		event(line, column, key, new Object[0]);
	}
	
	public void event(int line, int column, String key, Object[] args) {
		task.log(fileName + ":" + line + ":" + column + " " + MessageFormat.format(Resources.MESSAGES.getString(key), args) + " (" + key + ")");
	}
}
