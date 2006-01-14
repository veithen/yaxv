package net.sf.yaxv;

import org.apache.tools.ant.Task;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class YaxvErrorHandler implements ErrorHandler {
	private final Task task;
	private final String file;
	private int errorCount;
	
	public YaxvErrorHandler(Task task, String file) {
		this.task = task;
		this.file = file;
	}
	
	private void log(String tag, SAXParseException ex) {
		task.log("[" + tag + "] " + file + ":" + ex.getLineNumber() + ":" + ex.getColumnNumber() + " " + ex.getMessage());
	}
	
	public void warning(SAXParseException ex) throws SAXException {
		log("warning", ex);
	}

	public void error(SAXParseException ex) throws SAXException {
		log("error", ex);
		errorCount++;
	}

	public void fatalError(SAXParseException ex) throws SAXException {
		log("fatal", ex);
		errorCount++;
	}
	
	public int getErrorCount() { return errorCount; }
}