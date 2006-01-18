package net.sf.yaxv.css;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

public class CSSContextTracker extends DefaultHandler {
	private CSSContext currentContext;
	private CSSContext lastChildContext;
	
	private Locator locator;

	public void setDocumentLocator(Locator locator) { this.locator = locator; }

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		currentContext = new CSSContext(currentContext, lastChildContext, locator.getLineNumber(), locator.getColumnNumber(), localName, new AttributesImpl(attributes));
		lastChildContext = null;
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (lastChildContext != null) {
			discardContext(lastChildContext);
		}
		lastChildContext = currentContext;
		currentContext = currentContext.getParentContext();
	}
	
	public CSSContext getContext() { return currentContext; }
	
	private void discardContext(CSSContext context) {
		// TODO: process contexts in the reverse order
		do {
			if (context.getAttributes().getIndex("class") != -1 && !context.getMatchedAttributes().contains("class")) {
				System.out.println(context.getLineNumber() + ":" + context.getColumnNumber() + " Unused class attribute in element <" + context.getName() + " class=\"" + context.getAttributes().getValue("class") + "\">");
			}
		} while ((context = context.getSiblingContext()) != null);
	}
}
