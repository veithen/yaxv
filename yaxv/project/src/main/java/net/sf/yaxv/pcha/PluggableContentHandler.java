package net.sf.yaxv.pcha;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public interface PluggableContentHandler {
    void startDocument(PCHAContext context) throws SAXException;
    void endDocument(PCHAContext context) throws SAXException;
    void startPrefixMapping(PCHAContext context, String prefix, String uri) throws SAXException;
    void endPrefixMapping(PCHAContext context, String prefix) throws SAXException;
    void startElement(PCHAContext context, String uri, String localName, String qName, Attributes atts) throws SAXException;
    void endElement(PCHAContext context, String uri, String localName, String qName) throws SAXException;
    void characters(PCHAContext context, char[] ch, int start, int length) throws SAXException;
    void ignorableWhitespace(PCHAContext context, char[] ch, int start, int length) throws SAXException;
    void processingInstruction(PCHAContext context, String target, String data) throws SAXException;
    void skippedEntity(PCHAContext context, String name) throws SAXException;
}
