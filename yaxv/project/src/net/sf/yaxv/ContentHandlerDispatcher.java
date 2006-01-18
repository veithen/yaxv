package net.sf.yaxv;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class ContentHandlerDispatcher implements ContentHandler {
	private final List handlers = new LinkedList();
	
	public void addContentHandler(ContentHandler handler) {
		handlers.add(handler);
	}

	public void setDocumentLocator(Locator locator) {
		for (Iterator it = handlers.iterator(); it.hasNext(); ) {
			((ContentHandler)it.next()).setDocumentLocator(locator);
		}
	}

	public void startDocument() throws SAXException {
		for (Iterator it = handlers.iterator(); it.hasNext(); ) {
			((ContentHandler)it.next()).startDocument();
		}
	}

	public void endDocument() throws SAXException {
		for (Iterator it = handlers.iterator(); it.hasNext(); ) {
			((ContentHandler)it.next()).endDocument();
		}
	}

	public void startPrefixMapping(String prefix, String uri) throws SAXException {
		for (Iterator it = handlers.iterator(); it.hasNext(); ) {
			((ContentHandler)it.next()).startPrefixMapping(prefix, uri);
		}
	}

	public void endPrefixMapping(String prefix) throws SAXException {
		for (Iterator it = handlers.iterator(); it.hasNext(); ) {
			((ContentHandler)it.next()).endPrefixMapping(prefix);
		}
	}

	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		for (Iterator it = handlers.iterator(); it.hasNext(); ) {
			((ContentHandler)it.next()).startElement(uri, localName, qName, atts);
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		for (Iterator it = handlers.iterator(); it.hasNext(); ) {
			((ContentHandler)it.next()).endElement(uri, localName, qName);
		}
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		for (Iterator it = handlers.iterator(); it.hasNext(); ) {
			((ContentHandler)it.next()).characters(ch, start, length);
		}
	}

	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
		for (Iterator it = handlers.iterator(); it.hasNext(); ) {
			((ContentHandler)it.next()).ignorableWhitespace(ch, start, length);
		}
	}

	public void processingInstruction(String target, String data) throws SAXException {
		for (Iterator it = handlers.iterator(); it.hasNext(); ) {
			((ContentHandler)it.next()).processingInstruction(target, data);
		}
	}

	public void skippedEntity(String name) throws SAXException {
		for (Iterator it = handlers.iterator(); it.hasNext(); ) {
			((ContentHandler)it.next()).skippedEntity(name);
		}
	}
}
