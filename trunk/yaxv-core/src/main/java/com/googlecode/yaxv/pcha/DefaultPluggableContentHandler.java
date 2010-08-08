package com.googlecode.yaxv.pcha;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class DefaultPluggableContentHandler implements PluggableContentHandler {
    public void startDocument(PCHAContext context) throws SAXException {}
    public void endDocument(PCHAContext context) throws SAXException {}
    public void startPrefixMapping(PCHAContext context, String prefix, String uri) throws SAXException {}
    public void endPrefixMapping(PCHAContext context, String prefix) throws SAXException {}
    public void startElement(PCHAContext context, String uri, String localName, String qName, Attributes atts) throws SAXException {}
    public void endElement(PCHAContext context, String uri, String localName, String qName) throws SAXException {}
    public void characters(PCHAContext context, char[] ch, int start, int length) throws SAXException {}
    public void ignorableWhitespace(PCHAContext context, char[] ch, int start, int length) throws SAXException {}
    public void processingInstruction(PCHAContext context, String target, String data) throws SAXException {}
    public void skippedEntity(PCHAContext context, String name) throws SAXException {}
}
