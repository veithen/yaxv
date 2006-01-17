package net.sf.yaxv;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.sf.yaxv.css.CSSParserException;
import net.sf.yaxv.css.Parser;
import net.sf.yaxv.css.ParserEventListener;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

public class CssValidatorTask extends Task {
	private static class LocalParserEventListener implements ParserEventListener {
		private final Task task;
		private final String fileName;
		
		public LocalParserEventListener(Task task, String fileName) {
			this.task = task;
			this.fileName = fileName;
		}
		
		public int event(int level, int line, int column, String key) {
			task.log(fileName + ":" + line + ":" + column + " " + Resources.MESSAGES.getString(key) + " (" + key + ")");
			return ACTION_CONTINUE;
		}
	}
	
	private final List filesets = new LinkedList();
	
	public void add(FileSet fileset) { filesets.add(fileset); }

	public void execute() throws BuildException {
		Project project = getProject();
		Parser cssParser = new Parser();
		for (Iterator it = filesets.iterator(); it.hasNext(); ) {
			FileSet fileSet = (FileSet)it.next();
			File dir = fileSet.getDir(project);
			String[] files = fileSet.getDirectoryScanner(project).getIncludedFiles();
			for (int i=0; i<files.length; i++) {
				String fileName = files[i];
				try {
					cssParser.setEventListener(new LocalParserEventListener(this, fileName));
					cssParser.parseStylesheet(new FileInputStream(new File(dir, fileName)));
				}
				catch (IOException ex) {
					throw new BuildException("Unable to read " + fileName + ": " + ex.getMessage());
				}
				catch (CSSParserException ex) {
					throw new BuildException(fileName + ":" + ex.getLineNumber() + ":" + ex.getColumnNumber() + " " + ex.getMessage() + " (" + ex.getKey() + ")");
				}
			}
		}
	}
}
