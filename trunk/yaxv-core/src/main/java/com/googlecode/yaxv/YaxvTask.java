package com.googlecode.yaxv;

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
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.googlecode.yaxv.css.CSSContextTracker;
import com.googlecode.yaxv.css.HTMLStyleHandler;
import com.googlecode.yaxv.css.Parser;
import com.googlecode.yaxv.css.StylesheetCache;
import com.googlecode.yaxv.html.HTMLURIResolver;
import com.googlecode.yaxv.pcha.ContentHandlerSet;
import com.googlecode.yaxv.url.LinkValidationEngine;

// TODO: check that file:// URLs do not point to files outside of the site directory
// TODO: restrict URL protocols per tag/attribute (<img src="mailto:xxx"/> is invalid)
// TODO: check content types (<img src="..."/> can only point to image/*)
public class YaxvTask extends Task {
    private final List<FileSet> filesets = new LinkedList<FileSet>();
    
    private File linkcache;
    
    public void setLinkcache(File value) { linkcache = value; }
    
    public void add(FileSet fileset) { filesets.add(fileset); }
    
    @Override
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
        
        // Set up the URL attribute set, i.e. the set of attributes that contain URLs that must be
        // validated.
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
        
        TaskEventListener taskEventListener = new TaskEventListener(this);
        
        // Set up link validation engine
        LinkValidationEngine linkValidationEngine = new LinkValidationEngine(10);
        if (linkcache != null && linkcache.exists()) {
            try {
                linkValidationEngine.loadCacheFile(linkcache);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        // Set up stylesheet cache
        StylesheetCache stylesheetCache = new StylesheetCache(new Parser(), new AntStylesheetCacheEventListener(taskEventListener));
        
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
                    FileEventListener listener = new FileEventListener(taskEventListener, fileName);
                    Parser cssParser = new Parser();
                    ContentHandlerSet contentHandlerSet = new ContentHandlerSet();
                    contentHandlerSet.addContentHandler(new LinkExtractor(urlAttributes, linkValidationEngine, errorListener, listener));
                    contentHandlerSet.addContentHandler(new CSSContextTracker());
                    contentHandlerSet.addContentHandler(new HTMLStyleHandler(stylesheetCache));
                    contentHandlerSet.addContentHandler(new HTMLURIResolver(file.toURI()));
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
                linkValidationEngine.flushProcessed();
            }
        }
        linkValidationEngine.finish();
        if (linkcache != null) {
            try {
                linkValidationEngine.writeCacheFile(linkcache);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        if (errorCount > 0) {
            log("Found " + errorCount + " errors");
            throw new BuildException("Validation failed");
        }
    }
}
