package net.sf.yaxv;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import net.sf.yaxv.url.URLValidationEngine;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

// TODO: check that file:// URLs do not point to files outside of the site directory
// TODO: restrict URL protocols per tag/attribute (<img src="mailto:xxx"/> is invalid)
// TODO: check content types (<img src="..."/> can only point to image/*)
public class YaxvTask extends Task {
	private static class MyContentHandler extends DefaultHandler {
		private final URL base;
		private final AttributeSet urlAttributes;
		private final URLValidationEngine urlValidationEngine;
		private final ErrorListener errorListener;
		
		private Locator locator;
		
		public MyContentHandler(URL base, AttributeSet urlAttributes, URLValidationEngine urlValidationEngine, ErrorListener errorListener) {
			this.base = base;
			this.urlAttributes = urlAttributes;
			this.urlValidationEngine = urlValidationEngine;
			this.errorListener = errorListener;
		}

		public void setDocumentLocator(Locator locator) { this.locator = locator; }
		
		private void log(int level, String message) {
			errorListener.log(level, locator.getLineNumber(), locator.getColumnNumber(), message);
		}
		
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			for (int i=0; i<attributes.getLength(); i++) {
				if (urlAttributes.contains(localName, attributes.getLocalName(i))) {
					try {
						URL url = new URL(base, attributes.getValue(i));
						String protocol = url.getProtocol();
						if (protocol.equals("mailto")) {
							// TODO: test this
							// Do nothing
						} else if (protocol.equals("file") || protocol.equals("http") || protocol.equals("https") || protocol.equals("ftp")) {
//							System.out.println("Link to check: " + url);
							urlValidationEngine.validate(url, errorListener);
						} else {
							log(ErrorListener.ERROR, "Invalid protocol in URL " + url);
						}
					}
					catch (MalformedURLException ex) {
						// TODO
						System.out.println("Malformed URL");
					}
				}
			}
		}
	}
	
	private final List filesets = new LinkedList();
	
	public void add(FileSet fileset) { filesets.add(fileset); }

	public void execute() throws BuildException {
		Project project = getProject();
		
		// Set up the SAX parser
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
		
		// Set up the entity resolver
		try {
			xmlReader.setEntityResolver(CatalogResolver.getInstance("net/sf/yaxv"));
		}
		catch (CatalogResolverException ex) {
			throw new BuildException("Unable to get catalog with XHTML DTDs: " + ex.getMessage());
		}
		
		// Set up the URL attribute set
		AttributeSet urlAttributes;
		try {
			urlAttributes = new AttributeSet(YaxvTask.class.getResource("urlattributes"));
		}
		catch (IOException ex) {
			throw new BuildException("Unable to read URL attribute set: " + ex.getMessage());
		}
		catch (AttributeSetException ex) {
			throw new BuildException("Unable to read URL attribute set: " + ex.getMessage());
		}
		
		// Set up URL validation engine
		URLValidationEngine urlValidationEngine = new URLValidationEngine(10);
		
		int errorCount = 0;
		for (Iterator it = filesets.iterator(); it.hasNext(); ) {
			FileSet fileSet = (FileSet)it.next();
			File dir = fileSet.getDir(project);
			String[] files = fileSet.getDirectoryScanner(project).getIncludedFiles();
			for (int i=0; i<files.length; i++) {
				String fileName = files[i];
				File file = new File(dir, fileName);
				ErrorListener errorListener = new ErrorListener(this, fileName);
				try {
					xmlReader.setErrorHandler(new YaxvErrorHandler(errorListener));
					xmlReader.setContentHandler(new MyContentHandler(file.toURL(), urlAttributes, urlValidationEngine, errorListener));
					xmlReader.parse(file.getAbsolutePath());
					errorCount += errorListener.getErrorCount();
				}
				catch (IOException ex) {
					throw new BuildException("Error reading file " + file + ": " + ex.getMessage());
				}
				catch (SAXException ex) {
					if (errorListener.getErrorCount() == 0) {
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
