package net.sf.yaxv;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class YaxvTask extends Task {
	private static class MyContentHandler extends DefaultHandler {
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
//			System.out.println(localName);
		}
	}
	
	private final List filesets = new LinkedList();
	
	public void add(FileSet fileset) { filesets.add(fileset); }

	public void execute() throws BuildException {
		Project project = getProject();
		XMLReader xmlReader;
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			factory.setValidating(true);
			xmlReader = factory.newSAXParser().getXMLReader();
		}
		catch (ParserConfigurationException ex) {
			throw new BuildException("Unable to get SAX parser", ex);
		}
		catch (SAXException ex) {
			throw new BuildException("Unable to get SAX parser", ex);
		}
		try {
			xmlReader.setEntityResolver(CatalogResolver.getInstance("net/sf/yaxv"));
		}
		catch (CatalogResolverException ex) {
			throw new BuildException("Unable to get catalog with XHTML DTDs: " + ex.getMessage());
		}
		int errorCount = 0;
		for (Iterator it = filesets.iterator(); it.hasNext(); ) {
			FileSet fileSet = (FileSet)it.next();
			File dir = fileSet.getDir(project);
			String[] files = fileSet.getDirectoryScanner(project).getIncludedFiles();
			for (int i=0; i<files.length; i++) {
				String fileName = files[i];
				File file = new File(dir, files[i]);
				YaxvErrorHandler eh = new YaxvErrorHandler(this, fileName);
				try {
					xmlReader.setErrorHandler(eh);
					xmlReader.setContentHandler(new MyContentHandler());
					xmlReader.parse(file.getAbsolutePath());
					errorCount += eh.getErrorCount();
				}
				catch (IOException ex) {
					throw new BuildException("Error reading file " + file + ": " + ex.getMessage());
				}
				catch (SAXException ex) {
					if (eh.getErrorCount() == 0) {
						log(fileName + ": " + ex.getMessage());
						errorCount++;
					}
				}
			}
		}
		if (errorCount > 0) {
			log("Found " + errorCount + " errors");
			throw new BuildException("Validation failed");
		}
	}
}
