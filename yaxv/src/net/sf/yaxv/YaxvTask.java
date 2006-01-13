package net.sf.yaxv;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

public class YaxvTask extends Task {
	private static class YaxvErrorHandler implements ErrorHandler {
		public void warning(SAXParseException ex) throws SAXException {
			throw ex;
		}

		public void error(SAXParseException ex) throws SAXException {
			throw ex;
		}

		public void fatalError(SAXParseException ex) throws SAXException {
			throw ex;
		}
	}
	
	private final List filesets = new LinkedList();
	
	public void add(FileSet fileset) { filesets.add(fileset); }

	public void execute() throws BuildException {
		XMLReader xmlReader;
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			xmlReader = factory.newSAXParser().getXMLReader();
		}
		catch (ParserConfigurationException ex) {
			throw new BuildException("Unable to get SAX parser", ex);
		}
		catch (SAXException ex) {
			throw new BuildException("Unable to get SAX parser", ex);
		}
		xmlReader.setErrorHandler(new YaxvErrorHandler());
		for (Iterator it = filesets.iterator(); it.hasNext(); ) {
			FileSet fileSet = (FileSet)it.next();
			File dir = fileSet.getDir(getProject());
			String[] files = fileSet.getDirectoryScanner(getProject()).getIncludedFiles();
			for (int i=0; i<files.length; i++) {
				File file = new File(dir, files[i]);
				try {
					xmlReader.parse(file.getAbsolutePath());
				}
				catch (IOException ex) {
					throw new BuildException("Error reading file " + file, ex);
				}
				catch (SAXException ex) {
					throw new BuildException("Error parsing file " + file, ex);
				}
			}
		}
	}
}
