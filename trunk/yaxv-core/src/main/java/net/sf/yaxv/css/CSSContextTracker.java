package net.sf.yaxv.css;

import net.sf.yaxv.pcha.DefaultPluggableContentHandler;
import net.sf.yaxv.pcha.PCHAContext;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class CSSContextTracker extends DefaultPluggableContentHandler {
    private CSSContext currentContext;
    private CSSContext lastChildContext;
    
    public void startElement(PCHAContext pchaContext, String uri, String localName, String qName, Attributes attributes) throws SAXException {
        Locator locator = pchaContext.getLocator();
        currentContext = new CSSContext(currentContext, lastChildContext, locator.getLineNumber(), locator.getColumnNumber(), localName, new AttributesImpl(attributes));
        lastChildContext = null;
    }

    public void endElement(PCHAContext pchaContext, String uri, String localName, String qName) throws SAXException {
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
