package net.sf.yaxv;

import net.sf.yaxv.css.ParserEventListener;
import org.apache.tools.ant.Task;

public class AntParserEventListener implements ParserEventListener {
	private final Task task;
	private final String fileName;
	
	public AntParserEventListener(Task task, String fileName) {
		this.task = task;
		this.fileName = fileName;
	}
	
	public int event(int level, int line, int column, String key) {
		task.log(fileName + ":" + line + ":" + column + " " + Resources.MESSAGES.getString(key)  + " (" + key + ")");
		return ACTION_CONTINUE;
	}
}