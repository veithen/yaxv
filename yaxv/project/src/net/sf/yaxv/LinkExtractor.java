package net.sf.yaxv;

import java.net.MalformedURLException;
import java.net.URL;
import net.sf.yaxv.pcha.DefaultPluggableContentHandler;
import net.sf.yaxv.pcha.PCHAContext;
import net.sf.yaxv.url.URLValidationEngine;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class LinkExtractor extends DefaultPluggableContentHandler {
	private final URL base;
	private final AttributeSet urlAttributes;
	private final URLValidationEngine urlValidationEngine;
	private final ErrorListener errorListener;
	
	public LinkExtractor(URL base, AttributeSet urlAttributes, URLValidationEngine urlValidationEngine, ErrorListener errorListener) {
		this.base = base;
		this.urlAttributes = urlAttributes;
		this.urlValidationEngine = urlValidationEngine;
		this.errorListener = errorListener;
	}

	private void log(PCHAContext context, int level, String message) {
		Locator locator = context.getLocator();
		errorListener.log(level, locator.getLineNumber(), locator.getColumnNumber(), message);
	}
	
	public void startElement(PCHAContext context, String uri, String localName, String qName, Attributes attributes) throws SAXException {
		for (int i = 0; i<attributes.getLength(); i++) {
			if (urlAttributes.contains(localName, attributes.getLocalName(i))) {
				try {
					URL url = new URL(base, attributes.getValue(i));
					String protocol = url.getProtocol();
					if (protocol.equals("mailto")) {
						// TODO: test this
						// Do nothing
					} else if (protocol.equals("file") || protocol.equals("http") || protocol.equals("https") || protocol.equals("ftp")) {
//							System.out.println("Link to check: " + url);
						urlValidationEngine.validate(url, errorListener);
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