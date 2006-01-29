package net.sf.yaxv;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import net.sf.yaxv.css.CSSContextTracker;
import net.sf.yaxv.css.HTMLStyleHandler;
import net.sf.yaxv.css.Parser;
import net.sf.yaxv.html.HTMLURLResolver;
import net.sf.yaxv.pcha.ContentHandlerSet;
import net.sf.yaxv.url.LinkValidationEngine;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

// TODO: check that file:// URLs do not point to files outside of the site directory
// TODO: restrict URL protocols per tag/attribute (<img src="mailto:xxx"/> is invalid)
// TODO: check content types (<img src="..."/> can only point to image/*)
public class YaxvTask extends Task {
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
		LinkValidationEngine urlValidationEngine = new LinkValidationEngine(10);
		
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
					log("Processing file " + fileName);
					FileEventListener listener = new FileEventListener(this, fileName);
					Parser cssParser = new Parser();
					cssParser.setEventListener(new AntParserEventListener(listener));
					ContentHandlerSet contentHandlerSet = new ContentHandlerSet();
					contentHandlerSet.addContentHandler(new LinkExtractor(urlAttributes, urlValidationEngine, errorListener, listener));
					contentHandlerSet.addContentHandler(new CSSContextTracker());
					contentHandlerSet.addContentHandler(new HTMLStyleHandler(cssParser));
					contentHandlerSet.addContentHandler(new HTMLURLResolver(file.toURL()));
					xmlReader.setErrorHandler(new YaxvErrorHandler(errorListener));
					xmlReader.setContentHandler(contentHandlerSet);
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
				catch (Throwable ex) {
					ex.printStackTrace();
					throw new BuildException("Unexpected exception: " + ex.getMessage());
				}
				urlValidationEngine.flushProcessed();
			}
		}
		urlValidationEngine.finish();
		if (errorCount > 0) {
			log("Found " + errorCount + " errors");
			throw new BuildException("Validation failed");
		}
	}
}
