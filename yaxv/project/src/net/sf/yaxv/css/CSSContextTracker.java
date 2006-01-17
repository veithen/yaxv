package net.sf.yaxv.css;

import java.util.LinkedList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class CSSContextTracker extends DefaultHandler {
	private CSSContext currentContext;
	private CSSContext lastChildContext;
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		currentContext = new CSSContext(currentContext, lastChildContext);
		lastChildContext = null;
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		lastChildContext = currentContext;
		currentContext = currentContext.getParentContext();
	}
}
