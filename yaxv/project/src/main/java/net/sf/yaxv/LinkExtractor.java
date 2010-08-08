package net.sf.yaxv;

import java.net.URI;
import java.net.URISyntaxException;

import net.sf.yaxv.pcha.DefaultPluggableContentHandler;
import net.sf.yaxv.pcha.PCHAContext;
import net.sf.yaxv.pcha.URIResolver;
import net.sf.yaxv.url.LinkValidationEngine;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class LinkExtractor extends DefaultPluggableContentHandler {
    private final AttributeSet urlAttributes;
    private final LinkValidationEngine linkValidationEngine;
    private final ErrorListener errorListener;
    private final FileEventListener listener;
    
    public LinkExtractor(AttributeSet urlAttributes, LinkValidationEngine linkValidationEngine, ErrorListener errorListener, FileEventListener listener) {
        this.urlAttributes = urlAttributes;
        this.linkValidationEngine = linkValidationEngine;
        this.errorListener = errorListener;
        this.listener = listener;
    }

    private void log(PCHAContext context, int level, String message) {
        Locator locator = context.getLocator();
        errorListener.log(level, locator.getLineNumber(), locator.getColumnNumber(), message);
    }
    
    public void startElement(PCHAContext context, String namespace, String localName, String qName, Attributes attributes) throws SAXException {
        for (int i = 0; i<attributes.getLength(); i++) {
            if (urlAttributes.contains(localName, attributes.getLocalName(i))) {
                try {
                    URI uri = ((URIResolver)context.getContentHandler(URIResolver.class)).resolveURI(attributes.getValue(i));
                    String scheme = uri.getScheme();
                    if (scheme.equals("mailto")) {
                        // TODO: test this
                        // Do nothing
                    } else if (scheme.equals("file") || scheme.equals("http") || scheme.equals("https") || scheme.equals("ftp")) {
//                            System.out.println("Link to check: " + url);
                        Locator locator = context.getLocator();
                        linkValidationEngine.validateLink(uri, new AntLinkValidationEventListener(listener, locator.getLineNumber(), locator.getColumnNumber()));
                    } else {
                        log(context, ErrorListener.ERROR, "Invalid scheme in URI " + uri);
                    }
                } catch (URISyntaxException ex) {
                    // TODO
                    System.out.println("Malformed URI");
                }
            }
        }
    }
}
