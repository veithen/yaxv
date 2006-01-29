package net.sf.yaxv;

import java.net.MalformedURLException;
import java.net.URL;
import net.sf.yaxv.pcha.DefaultPluggableContentHandler;
import net.sf.yaxv.pcha.PCHAContext;
import net.sf.yaxv.pcha.URLResolver;
import net.sf.yaxv.url.LinkValidationEngine;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class LinkExtractor extends DefaultPluggableContentHandler {
	private final AttributeSet urlAttributes;
	private final LinkValidationEngine urlValidationEngine;
	private final ErrorListener errorListener;
	private final FileEventListener listener;
	
	public LinkExtractor(AttributeSet urlAttributes, LinkValidationEngine urlValidationEngine, ErrorListener errorListener, FileEventListener listener) {
		this.urlAttributes = urlAttributes;
		this.urlValidationEngine = urlValidationEngine;
		this.errorListener = errorListener;
		this.listener = listener;
	}

	private void log(PCHAContext context, int level, String message) {
		Locator locator = context.getLocator();
		errorListener.log(level, locator.getLineNumber(), locator.getColumnNumber(), message);
	}
	
	public void startElement(PCHAContext context, String uri, String localName, String qName, Attributes attributes) throws SAXException {
		for (int i = 0; i<attributes.getLength(); i++) {
			if (urlAttributes.contains(localName, attributes.getLocalName(i))) {
				try {
					URL url = ((URLResolver)context.getContentHandler(URLResolver.class)).resolveUrl(attributes.getValue(i));
					String protocol = url.getProtocol();
					if (protocol.equals("mailto")) {
						// TODO: test this
						// Do nothing
					} else if (protocol.equals("file") || protocol.equals("http") || protocol.equals("https") || protocol.equals("ftp")) {
//							System.out.println("Link to check: " + url);
						Locator locator = context.getLocator();
						urlValidationEngine.validateLink(url, new AntLinkValidationEventListener(listener, locator.getLineNumber(), locator.getColumnNumber()));
					} else {
						log(context, ErrorListener.ERROR, "Invalid protocol in URL " + url);
					}
				} catch (MalformedURLException ex) {
					// TODO
					System.out.println("Malformed URL");
				}
			}
		}
	}
}