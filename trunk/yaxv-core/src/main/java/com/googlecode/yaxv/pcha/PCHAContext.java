package com.googlecode.yaxv.pcha;

import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public interface PCHAContext {
    <T extends PluggableContentHandler> T getContentHandler(Class<T> contentHandlerClass) throws SAXException;
    Locator getLocator();
}
