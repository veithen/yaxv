package net.sf.yaxv.pcha;

import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public interface PCHAContext {
    PluggableContentHandler getContentHandler(Class contentHandlerClass) throws SAXException;
    Locator getLocator();
}
