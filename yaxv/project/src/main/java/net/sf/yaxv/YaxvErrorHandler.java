package net.sf.yaxv;

import org.apache.tools.ant.Task;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class YaxvErrorHandler implements ErrorHandler {
    private final ErrorListener errorListener;
    
    public YaxvErrorHandler(ErrorListener errorListener) {
        this.errorListener = errorListener;
    }
    
    private void log(int level, SAXParseException ex) {
        errorListener.log(level, ex.getLineNumber(), ex.getColumnNumber(), ex.getMessage());
    }
    
    public void warning(SAXParseException ex) throws SAXException {
        log(ErrorListener.WARNING, ex);
    }

    public void error(SAXParseException ex) throws SAXException {
        log(ErrorListener.ERROR, ex);
    }

    public void fatalError(SAXParseException ex) throws SAXException {
        log(ErrorListener.FATAL, ex);
    }
}
